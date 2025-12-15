document.addEventListener('DOMContentLoaded', function () {
    const bubble = document.getElementById('ai-chat-bubble');
    const windowEl = document.getElementById('ai-chat-window');
    const teaser = document.getElementById('ai-chat-teaser');
    const closeTeaserBtn = document.getElementById('ai-chat-teaser-close');
    const closeBtn = document.getElementById('ai-chat-close');
    const minimizeBtn = document.getElementById('ai-chat-minimize');
    const messagesContainer = document.querySelector('.ai-chat-messages');

    if (!bubble || !windowEl || !messagesContainer) {
        return;
    }

    let isOpen = false;
    let isMinimized = false;

    // Estado para recomendaciones de conjuntos (fijar prendas)
    let prendasFijas = {
        superiorId: null,
        inferiorId: null,
        calzadoId: null
    };

    // Estado del modo actual y filtros de la sesión del chat
    let estadoModo = {
        modo: null,
        colorSeleccionado: null,
        marcaSeleccionada: null,
        tiempo: null,
        ocasion: null,
        prendasEvitar: new Set(),  // ids para modo SIN_REPETIR (no se usa ya para lógica fuerte)
        conjuntosUsados: new Set() // claves "supId-infId-calId" ya mostradas en esta sesión
    };

    // Cache local para opciones reales de color/marca
    let cacheOpcionesArmario = {
        colores: [],
        marcas: [],
        cargado: false
    };

    const showWindow = () => {
        windowEl.classList.add('ai-chat-window--open');
        windowEl.classList.remove('ai-chat-window--hidden');
        isOpen = true;
        isMinimized = false;
        if (teaser) {
            teaser.classList.add('ai-chat-teaser--hidden');
        }
    };

    const hideWindow = () => {
        windowEl.classList.remove('ai-chat-window--open');
        windowEl.classList.add('ai-chat-window--hidden');
        isOpen = false;
    };

    const minimizeWindow = () => {
        windowEl.classList.remove('ai-chat-window--open');
        windowEl.classList.add('ai-chat-window--minimized');
        isMinimized = true;
    };

    const scrollToBottom = () => {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    };

    const appendMessage = (text, type) => {
        const msg = document.createElement('div');
        msg.classList.add('ai-chat-message', `ai-chat-message--${type}`);
        msg.textContent = text;
        messagesContainer.appendChild(msg);
        scrollToBottom();
    };

    const resetModo = () => {
        estadoModo.modo = null;
        estadoModo.colorSeleccionado = null;
        estadoModo.marcaSeleccionada = null;
        estadoModo.tiempo = null;
        estadoModo.ocasion = null;
        estadoModo.prendasEvitar = new Set();
    };

    const crearGrupoBotones = (opciones, onSelect) => {
        const group = document.createElement('div');
        group.className = 'ai-outfit-actions';
        group.style.marginTop = '0.4rem';
        opciones.forEach(op => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'ai-outfit-btn';
            btn.textContent = op.label;
            btn.addEventListener('click', function () {
                onSelect(op, group, btn);
            });
            group.appendChild(btn);
        });
        return group;
    };

    const marcarSeleccionEnGrupo = (groupEl, btnSeleccionado) => {
        Array.from(groupEl.querySelectorAll('button')).forEach(b => {
            b.disabled = true;
            b.classList.remove('ai-outfit-btn--primary');
        });
        if (btnSeleccionado) {
            btnSeleccionado.classList.add('ai-outfit-btn--primary');
        }
    };

    // --- Menú principal de modos ---
    const mostrarMenuModos = () => {
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = '¿Cómo quieres que te recomiende hoy?';
        card.appendChild(titulo);

        const modos = [
            { value: 'SORPRESA', label: 'Conjunto sorpresa' },
            { value: 'COLOR', label: 'Por color' },
            { value: 'MARCA', label: 'Por marca' },
            { value: 'TIEMPO', label: 'Por tiempo' },
            { value: 'OCASION', label: 'Por ocasión' },
            { value: 'SIN_REPETIR', label: 'Sin repetir' },
            { value: 'COMPLETAR', label: 'Completar esta prenda' }
        ];

        const group = crearGrupoBotones(modos, (op, groupEl, btn) => {
            marcarSeleccionEnGrupo(groupEl, btn);
            estadoModo.modo = op.value;
            if (op.value === 'SORPRESA' || op.value === 'SIN_REPETIR') {
                solicitarRecomendacion();
            } else if (op.value === 'COLOR') {
                preguntarColor();
            } else if (op.value === 'MARCA') {
                preguntarMarca();
            } else if (op.value === 'TIEMPO') {
                preguntarTiempo();
            } else if (op.value === 'OCASION') {
                preguntarOcasion();
            } else if (op.value === 'COMPLETAR') {
                appendMessage('Fija primero la prenda que quieras mantener (superior, inferior o calzado) y luego pulsa "Otra sugerencia".', 'bot');
                solicitarRecomendacion();
            }
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    // --- Preguntas por modo ---
    const preguntarColor = () => {
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = 'Elige un color de tu armario';
        card.appendChild(titulo);

        const renderOpciones = (colores) => {
            if (!colores || colores.length === 0) {
                const msg = document.createElement('div');
                msg.className = 'ai-outfit-warning';
                msg.textContent = 'No he encontrado colores guardados en tu armario.';
                card.appendChild(msg);
                messagesContainer.appendChild(card);
                scrollToBottom();
                return;
            }
            const opciones = colores.slice(0, 12).map(c => ({ value: c, label: c }));
            const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
                estadoModo.colorSeleccionado = op.value;
                marcarSeleccionEnGrupo(groupEl, btn);
                solicitarRecomendacion();
            });
            card.appendChild(group);
            messagesContainer.appendChild(card);
            scrollToBottom();
        };

        if (cacheOpcionesArmario.cargado) {
            renderOpciones(cacheOpcionesArmario.colores);
        } else {
            fetch('/api/chat/opciones-armario', { method: 'GET' })
                .then(r => r.ok ? r.json() : { colores: [], marcas: [] })
                .then(data => {
                    cacheOpcionesArmario.colores = data.colores || [];
                    cacheOpcionesArmario.marcas = data.marcas || [];
                    cacheOpcionesArmario.cargado = true;
                    renderOpciones(cacheOpcionesArmario.colores);
                })
                .catch(() => {
                    renderOpciones([]);
                });
        }
    };

    const preguntarMarca = () => {
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = 'Elige una marca de tu armario';
        card.appendChild(titulo);

        const renderOpciones = (marcas) => {
            if (!marcas || marcas.length === 0) {
                const msg = document.createElement('div');
                msg.className = 'ai-outfit-warning';
                msg.textContent = 'No he encontrado marcas guardadas en tu armario.';
                card.appendChild(msg);
                messagesContainer.appendChild(card);
                scrollToBottom();
                return;
            }
            const opciones = marcas.slice(0, 12).map(m => ({ value: m, label: m }));
            const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
                estadoModo.marcaSeleccionada = op.value;
                marcarSeleccionEnGrupo(groupEl, btn);
                solicitarRecomendacion();
            });
            card.appendChild(group);
            messagesContainer.appendChild(card);
            scrollToBottom();
        };

        if (cacheOpcionesArmario.cargado) {
            renderOpciones(cacheOpcionesArmario.marcas);
        } else {
            fetch('/api/chat/opciones-armario', { method: 'GET' })
                .then(r => r.ok ? r.json() : { colores: [], marcas: [] })
                .then(data => {
                    cacheOpcionesArmario.colores = data.colores || [];
                    cacheOpcionesArmario.marcas = data.marcas || [];
                    cacheOpcionesArmario.cargado = true;
                    renderOpciones(cacheOpcionesArmario.marcas);
                })
                .catch(() => {
                    renderOpciones([]);
                });
        }
    };

    const preguntarTiempo = () => {
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = '¿Cómo está el tiempo?';
        card.appendChild(titulo);

        const opciones = [
            { value: 'FRIO', label: 'Frío' },
            { value: 'TEMPLADO', label: 'Templado' },
            { value: 'CALOR', label: 'Calor' }
        ];

        const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
            estadoModo.tiempo = op.value;
            marcarSeleccionEnGrupo(groupEl, btn);
            solicitarRecomendacion();
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    const preguntarOcasion = () => {
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = '¿Para qué ocasión es el conjunto?';
        card.appendChild(titulo);

        const opciones = [
            { value: 'TRABAJO', label: 'Trabajo' },
            { value: 'FIESTA', label: 'Fiesta' },
            { value: 'DEPORTE', label: 'Deporte' },
            { value: 'CASUAL', label: 'Casual' }
        ];

        const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
            estadoModo.ocasion = op.value;
            marcarSeleccionEnGrupo(groupEl, btn);
            solicitarRecomendacion();
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    // --- Render de columnas de prenda, manteniendo botón de fijar ---
    const renderPrendaColumn = (prenda, tipoClave) => {
        const col = document.createElement('div');
        col.className = 'ai-outfit-prenda';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-prenda-titulo';
        if (tipoClave === 'superior') titulo.textContent = 'Superior';
        else if (tipoClave === 'inferior') titulo.textContent = 'Inferior';
        else if (tipoClave === 'calzado') titulo.textContent = 'Calzado';
        col.appendChild(titulo);

        const imgWrapper = document.createElement('div');
        imgWrapper.className = 'ai-outfit-prenda-img';
        if (prenda && prenda.imagenUrl) {
            const img = document.createElement('img');
            img.src = prenda.imagenUrl;
            img.alt = prenda.nombre || '';
            img.onerror = function () {
                this.onerror = null;
                this.src = '/images/placeholder_cloth.png';
            };
            imgWrapper.appendChild(img);
        } else {
            const placeholder = document.createElement('div');
            placeholder.className = 'ai-outfit-prenda-placeholder';
            placeholder.textContent = 'Sin imagen';
            imgWrapper.appendChild(placeholder);
        }
        col.appendChild(imgWrapper);

        if (prenda) {
            const meta = document.createElement('div');
            meta.className = 'ai-outfit-prenda-meta';
            const nombre = document.createElement('div');
            nombre.textContent = prenda.nombre || '';
            const detalles = document.createElement('div');
            detalles.className = 'ai-outfit-prenda-detalles';
            const partes = [];
            if (prenda.marca) partes.push(prenda.marca);
            if (prenda.color) partes.push(prenda.color);
            if (prenda.categoria) partes.push(prenda.categoria);
            detalles.textContent = partes.join(' · ');
            meta.appendChild(nombre);
            meta.appendChild(detalles);
            col.appendChild(meta);

            const fixBtn = document.createElement('button');
            fixBtn.type = 'button';
            fixBtn.className = 'ai-outfit-fix-btn';
            const yaFijada = prendasFijas[`${tipoClave}Id`] === prenda.id;
            fixBtn.textContent = yaFijada ? 'Fijada' : 'Fijar';
            if (yaFijada) {
                fixBtn.classList.add('ai-outfit-fix-btn--active');
            }
            fixBtn.addEventListener('click', function () {
                if (prendasFijas[`${tipoClave}Id`] === prenda.id) {
                    prendasFijas[`${tipoClave}Id`] = null;
                    fixBtn.textContent = 'Fijar';
                    fixBtn.classList.remove('ai-outfit-fix-btn--active');
                } else {
                    prendasFijas[`${tipoClave}Id`] = prenda.id;
                    fixBtn.textContent = 'Fijada';
                    fixBtn.classList.add('ai-outfit-fix-btn--active');
                }
            });
            col.appendChild(fixBtn);
        }

        return col;
    };

    const appendOutfitCard = (recomendacion) => {
        // Actualizar estado de "sin repetir": añadir conjunto usado (trío completo)
        if (estadoModo.modo === 'SIN_REPETIR' && recomendacion.superior && recomendacion.inferior && recomendacion.calzado) {
            const clave = `${recomendacion.superior.id}-${recomendacion.inferior.id}-${recomendacion.calzado.id}`;
            estadoModo.conjuntosUsados.add(clave);
        }

        const wrapper = document.createElement('div');
        wrapper.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const grid = document.createElement('div');
        grid.className = 'ai-outfit-grid';
        grid.appendChild(renderPrendaColumn(recomendacion.superior, 'superior'));
        grid.appendChild(renderPrendaColumn(recomendacion.inferior, 'inferior'));
        grid.appendChild(renderPrendaColumn(recomendacion.calzado, 'calzado'));
        wrapper.appendChild(grid);

        const explanation = document.createElement('div');
        explanation.className = 'ai-outfit-explicacion';
        explanation.textContent = recomendacion.mensaje || '';
        wrapper.appendChild(explanation);

        if (Array.isArray(recomendacion.faltantes) && recomendacion.faltantes.length > 0) {
            const warning = document.createElement('div');
            warning.className = 'ai-outfit-warning';
            if (recomendacion.faltantes.length === 3) {
                warning.textContent = 'Necesitas al menos una prenda superior, una inferior y calzado para poder generar conjuntos.';
            } else {
                warning.textContent = 'Te faltan prendas para crear conjuntos completos: ' + recomendacion.faltantes.join(', ') + '.';
            }
            wrapper.appendChild(warning);
        }

        const actions = document.createElement('div');
        actions.className = 'ai-outfit-actions';

        const otraBtn = document.createElement('button');
        otraBtn.type = 'button';
        otraBtn.className = 'ai-outfit-btn';
        otraBtn.textContent = 'Otra sugerencia';
        otraBtn.addEventListener('click', function () {
            // repetir el mismo modo + filtros + prendas fijadas
            solicitarRecomendacion();
        });

        const guardarBtn = document.createElement('button');
        guardarBtn.type = 'button';
        guardarBtn.className = 'ai-outfit-btn ai-outfit-btn--primary';
        guardarBtn.textContent = 'Guardar conjunto';
        guardarBtn.addEventListener('click', function () {
            mostrarFormularioGuardado(wrapper, recomendacion);
        });

        if (Array.isArray(recomendacion.faltantes) && recomendacion.faltantes.length > 0) {
            guardarBtn.disabled = true;
        }

        actions.appendChild(otraBtn);
        actions.appendChild(guardarBtn);
        wrapper.appendChild(actions);

        messagesContainer.appendChild(wrapper);
        scrollToBottom();
    };

    const mostrarFormularioGuardado = (cardEl, recomendacion) => {
        if (!recomendacion || !recomendacion.superior || !recomendacion.inferior || !recomendacion.calzado) {
            return;
        }

        // Evitar duplicar formulario en la misma card
        if (cardEl.querySelector('.ai-outfit-guardar-form')) {
            return;
        }

        const formWrapper = document.createElement('div');
        formWrapper.className = 'ai-outfit-guardar-form';

        const titulo = document.createElement('div');
        titulo.textContent = 'Guardar este conjunto en tus conjuntos';
        formWrapper.appendChild(titulo);

        const nombreInput = document.createElement('input');
        nombreInput.type = 'text';
        nombreInput.placeholder = 'Nombre del conjunto (obligatorio)';
        formWrapper.appendChild(nombreInput);

        const notasInput = document.createElement('textarea');
        notasInput.placeholder = 'Notas (opcional)';
        formWrapper.appendChild(notasInput);

        const errorEl = document.createElement('div');
        errorEl.className = 'ai-outfit-error';
        formWrapper.appendChild(errorEl);

        const formActions = document.createElement('div');
        formActions.className = 'ai-outfit-guardar-actions';

        const cancelarBtn = document.createElement('button');
        cancelarBtn.type = 'button';
        cancelarBtn.textContent = 'Cancelar';
        cancelarBtn.addEventListener('click', function () {
            formWrapper.remove();
        });

        const confirmarBtn = document.createElement('button');
        confirmarBtn.type = 'button';
        confirmarBtn.className = 'ai-outfit-btn ai-outfit-btn--primary';
        confirmarBtn.textContent = 'Confirmar guardado';
        confirmarBtn.addEventListener('click', function () {
            const nombre = (nombreInput.value || '').trim();
            const notas = (notasInput.value || '').trim();
            if (!nombre) {
                errorEl.textContent = 'El conjunto necesita un nombre.';
                return;
            }
            errorEl.textContent = '';
            confirmarBtn.disabled = true;

            fetch('/api/chat/guardar-conjunto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    prendaSuperiorId: recomendacion.superior.id,
                    prendaInferiorId: recomendacion.inferior.id,
                    prendaCalzadoId: recomendacion.calzado.id,
                    nombre: nombre,
                    notas: notas
                })
            })
                .then(function (response) {
                    if (!response.ok) {
                        return response.json().catch(function () {
                            throw new Error('Error en la respuesta del servidor');
                        }).then(function (data) {
                            const msg = data && data.mensaje ? data.mensaje : 'Ha ocurrido un error al guardar el conjunto.';
                            throw new Error(msg);
                        });
                    }
                    return response.json();
                })
                .then(function (data) {
                    if (data && data.ok) {
                        appendMessage(data.mensaje || 'Conjunto guardado correctamente.', 'bot');
                        formWrapper.remove();
                    } else {
                        errorEl.textContent = (data && data.mensaje) || 'No se ha podido guardar el conjunto.';
                        confirmarBtn.disabled = false;
                    }
                })
                .catch(function (err) {
                    errorEl.textContent = err.message || 'Ha ocurrido un error al guardar el conjunto.';
                    confirmarBtn.disabled = false;
                });
        });

        formActions.appendChild(cancelarBtn);
        formActions.appendChild(confirmarBtn);
        formWrapper.appendChild(formActions);

        cardEl.appendChild(formWrapper);
        scrollToBottom();
    };

    // --- Petición al backend teniendo en cuenta modo y filtros ---
    const solicitarRecomendacion = () => {
        const typingId = `typing-${Date.now()}`;
        const typingMsg = document.createElement('div');
        typingMsg.classList.add('ai-chat-message', 'ai-chat-message--bot', 'ai-chat-message--typing');
        typingMsg.id = typingId;
        typingMsg.textContent = 'Estoy buscando un conjunto en tu armario...';
        messagesContainer.appendChild(typingMsg);
        scrollToBottom();

        const bodyRequest = {
            superiorFijoId: prendasFijas.superiorId,
            inferiorFijoId: prendasFijas.inferiorId,
            calzadoFijoId: prendasFijas.calzadoId,
            modo: estadoModo.modo,
            colorFiltro: estadoModo.colorSeleccionado,
            marcaFiltro: estadoModo.marcaSeleccionada,
            tiempo: estadoModo.tiempo,
            ocasion: estadoModo.ocasion,
            prendasEvitarIds: Array.from(estadoModo.prendasEvitar),
            conjuntosUsados: Array.from(estadoModo.conjuntosUsados)
        };

        fetch('/api/chat/recomendar-conjunto', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bodyRequest)
        })
            .then(function (response) {
                if (!response.ok) {
                    return response.json().catch(function () {
                        throw new Error('Error en la respuesta del servidor');
                    }).then(function (data) {
                        const msg = data && data.mensaje ? data.mensaje : 'No he podido generar una recomendación.';
                        throw new Error(msg);
                    });
                }
                return response.json();
            })
            .then(function (data) {
                if (typingMsg.parentNode) {
                    typingMsg.parentNode.removeChild(typingMsg);
                }
                if (!data) {
                    appendMessage('No he podido generar una recomendación ahora mismo.', 'bot');
                    return;
                }
                appendOutfitCard(data);
            })
            .catch(function (err) {
                if (typingMsg.parentNode) {
                    typingMsg.parentNode.removeChild(typingMsg);
                }
                appendMessage(err.message || 'Ha ocurrido un error al generar la recomendación.', 'bot');
            });
    };

    // --- Botones globales del widget ---
    const crearBotonRecomendar = () => {
        if (document.getElementById('ai-chat-recomendar-btn')) {
            return;
        }
        const body = windowEl.querySelector('.ai-chat-body');
        if (!body) return;

        const actions = document.createElement('div');
        actions.style.marginTop = '0.5rem';
        actions.style.display = 'flex';
        actions.style.justifyContent = 'space-between';
        actions.style.alignItems = 'center';

        const leftGroup = document.createElement('div');
        const rightGroup = document.createElement('div');

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'ai-chat-recomendar-btn';
        btn.className = 'ai-chat-recomendar-btn';
        btn.textContent = 'Recomiéndame…';
        btn.addEventListener('click', function () {
            mostrarMenuModos();
        });

        const desfijarBtn = document.createElement('button');
        desfijarBtn.type = 'button';
        desfijarBtn.className = 'ai-chat-recomendar-btn';
        desfijarBtn.textContent = 'Desfijar todo';
        desfijarBtn.addEventListener('click', function () {
            prendasFijas.superiorId = null;
            prendasFijas.inferiorId = null;
            prendasFijas.calzadoId = null;
            appendMessage('He quitado todas las prendas fijadas. Las siguientes sugerencias serán totalmente libres (según el modo que elijas).', 'bot');
        });

        leftGroup.appendChild(btn);
        rightGroup.appendChild(desfijarBtn);

        actions.appendChild(leftGroup);
        actions.appendChild(rightGroup);
        body.appendChild(actions);
    };

    // --- Eventos de abrir/cerrar/minimizar ventana ---
    bubble.addEventListener('click', function () {
        if (!isOpen || isMinimized) {
            windowEl.classList.remove('ai-chat-window--minimized');
            showWindow();
        } else {
            minimizeWindow();
        }
    });

    if (closeTeaserBtn && teaser) {
        closeTeaserBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            teaser.classList.add('ai-chat-teaser--hidden');
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', function () {
            hideWindow();
        });
    }

    if (minimizeBtn) {
        minimizeBtn.addEventListener('click', function () {
            minimizeWindow();
        });
    }

    // Mostrar teaser al cargar
    if (teaser) {
        teaser.classList.remove('ai-chat-teaser--hidden');
    }

    crearBotonRecomendar();
});
