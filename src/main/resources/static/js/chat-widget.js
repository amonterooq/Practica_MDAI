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

    // Estado para recomendaciones de conjuntos
    let prendasFijas = {
        superiorId: null,
        inferiorId: null,
        calzadoId: null
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

    const crearBotonRecomendar = () => {
        // Evitar duplicar el botón si ya existe
        if (document.getElementById('ai-chat-recomendar-btn')) {
            return;
        }
        const body = windowEl.querySelector('.ai-chat-body');
        if (!body) return;

        const actions = document.createElement('div');
        actions.style.marginTop = '0.5rem';
        actions.style.display = 'flex';
        actions.style.justifyContent = 'flex-end';

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'ai-chat-recomendar-btn';
        btn.className = 'ai-chat-recomendar-btn';
        btn.textContent = 'Recomendar conjunto';
        btn.addEventListener('click', function () {
            solicitarRecomendacion();
        });

        actions.appendChild(btn);
        body.appendChild(actions);
    };

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

    const solicitarRecomendacion = () => {
        const typingId = `typing-${Date.now()}`;
        const typingMsg = document.createElement('div');
        typingMsg.classList.add('ai-chat-message', 'ai-chat-message--bot', 'ai-chat-message--typing');
        typingMsg.id = typingId;
        typingMsg.textContent = 'Estoy buscando un conjunto en tu armario...';
        messagesContainer.appendChild(typingMsg);
        scrollToBottom();

        fetch('/api/chat/recomendar-conjunto', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                superiorFijoId: prendasFijas.superiorId,
                inferiorFijoId: prendasFijas.inferiorId,
                calzadoFijoId: prendasFijas.calzadoId
            })
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

    // Eventos de abrir/cerrar/minimizar ventana
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

    // Crear botón para recomendaciones de conjuntos cuando esté todo listo
    crearBotonRecomendar();
});
