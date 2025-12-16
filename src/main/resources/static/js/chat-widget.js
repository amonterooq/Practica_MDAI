document.addEventListener('DOMContentLoaded', function () {
    const bubble = document.getElementById('ai-chat-bubble');
    const windowEl = document.getElementById('ai-chat-window');
    const teaser = document.getElementById('ai-chat-teaser');
    const closeTeaserBtn = document.getElementById('ai-chat-teaser-close');
    const closeBtn = document.getElementById('ai-chat-close');
    const minimizeBtn = document.getElementById('ai-chat-minimize');
    const messagesContainer = document.querySelector('.ai-chat-messages');
    const greetingEl = document.querySelector('.ai-chat-greeting');

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
        conjuntosUsados: new Set(), // claves "supId-infId-calId" ya mostradas en esta sesión
        hayRecomendacionActiva: false,
        cargandoRecomendacion: false,
        // Nuevos campos para el flujo unificado de color/marca
        tipoCombinacion: null, // 'TODO' o 'COMBINADO'
        intensidad: null // 'PROTAGONISTA' o 'TOQUE'
    };

    // Referencia a la última card de recomendación para poder reemplazarla
    let ultimaCardRecomendacion = null;
    let barraAccionesGlobal = null;

    // Cache local para opciones reales de color/marca
    let cacheOpcionesArmario = {
        colores: [],
        marcas: [],
        cargado: false
    };

    let historialRecomendaciones = []; // pila de recomendaciones dentro del modo actual

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
        const wrapper = document.createElement('div');
        wrapper.classList.add('ai-chat-message-wrapper', `ai-chat-message-wrapper--${type}`);

        const msg = document.createElement('div');
        msg.classList.add('ai-chat-message', `ai-chat-message--${type}`);
        msg.textContent = text;

        wrapper.appendChild(msg);
        messagesContainer.appendChild(wrapper);
        scrollToBottom();
    };

    // Nueva función para mostrar la selección del usuario como burbuja
    const appendUserSelection = (text) => {
        const wrapper = document.createElement('div');
        wrapper.classList.add('ai-chat-message-wrapper', 'ai-chat-message-wrapper--user');

        const msg = document.createElement('div');
        msg.classList.add('ai-chat-message', 'ai-chat-message--user');
        msg.textContent = text;

        wrapper.appendChild(msg);
        messagesContainer.appendChild(wrapper);
        scrollToBottom();
    };

    const resetModo = () => {
        estadoModo.modo = null;
        estadoModo.colorSeleccionado = null;
        estadoModo.marcaSeleccionada = null;
        estadoModo.tiempo = null;
        estadoModo.ocasion = null;
        estadoModo.prendasEvitar = new Set();
        estadoModo.conjuntosUsados = new Set();
        estadoModo.hayRecomendacionActiva = false;
        estadoModo.cargandoRecomendacion = false;
        estadoModo.tipoCombinacion = null;
        estadoModo.intensidad = null;
        prendasFijas.superiorId = null;
        prendasFijas.inferiorId = null;
        prendasFijas.calzadoId = null;
        historialRecomendaciones = []; // limpiar historial al cambiar de modo
    };

    const limpiarRecomendacionActiva = () => {
        if (ultimaCardRecomendacion && ultimaCardRecomendacion.parentNode) {
            ultimaCardRecomendacion.parentNode.removeChild(ultimaCardRecomendacion);
        }
        ultimaCardRecomendacion = null;
        estadoModo.hayRecomendacionActiva = false;
        estadoModo.cargandoRecomendacion = false;
        // volver a mostrar la barra de acciones global cuando no hay recomendación
        if (barraAccionesGlobal) {
            barraAccionesGlobal.style.display = 'flex';
        }
    };

    const mostrarPantallaInicio = () => {
        // Limpiar mensajes del chat y cualquier recomendación visible
        messagesContainer.innerHTML = '';
        limpiarRecomendacionActiva();
        resetModo();
        // Mostrar de nuevo el saludo sólo en pantalla inicial
        if (greetingEl) {
            greetingEl.classList.remove('ai-chat-greeting--hidden');
        }
        if (barraAccionesGlobal) {
            barraAccionesGlobal.style.display = 'flex';
        }
    };

    const crearGrupoBotones = (opciones, onSelect) => {
        const group = document.createElement('div');
        group.className = 'ai-outfit-actions ai-outfit-actions--chips';
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

    const mostrarMenuModos = () => {
        limpiarRecomendacionActiva();
        resetModo();

        // Ocultar el saludo al entrar en el flujo de recomendaciones
        if (greetingEl) {
            greetingEl.classList.add('ai-chat-greeting--hidden');
        }

        // Ocultar el botón global "Recomiéndame…" al entrar en el flujo
        if (barraAccionesGlobal) {
            barraAccionesGlobal.style.display = 'none';
        }

        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';
        card.style.display = 'flex';
        card.style.flexDirection = 'column';
        card.style.alignItems = 'center';
        card.style.textAlign = 'center';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = '¿Cómo quieres que te recomiende hoy?';
        card.appendChild(titulo);

        const modos = [
            { value: 'SORPRESA', label: '🎲 Conjunto sorpresa' },
            { value: 'COLOR', label: '🎨 Por color' },
            { value: 'MARCA', label: '🏷️ Por marca' },
            { value: 'TIEMPO', label: '🌤️ Por tiempo' },
            { value: 'OCASION', label: '🎯 Por ocasión' },
            { value: 'COMPLETAR', label: '🧩 Completar esta prenda' }
        ];

        const group = crearGrupoBotones(modos, (op, groupEl, btn) => {
            marcarSeleccionEnGrupo(groupEl, btn);
            estadoModo.modo = op.value;

            // Mostrar la selección del usuario como burbuja
            appendUserSelection(op.label);

            if (op.value === 'SORPRESA') {
                solicitarRecomendacion();
            } else if (op.value === 'COLOR') {
                solicitarRecomendacionConPregunta(preguntarColor);
            } else if (op.value === 'MARCA') {
                solicitarRecomendacionConPregunta(preguntarMarca);
            } else if (op.value === 'TIEMPO') {
                solicitarRecomendacionConPregunta(preguntarTiempo);
            } else if (op.value === 'OCASION') {
                solicitarRecomendacionConPregunta(preguntarOcasion);
            } else if (op.value === 'COMPLETAR') {
                iniciarModoCompletar();
            }
        });

        // Centrar el grupo de botones como bloque
        group.style.justifyContent = 'center';

        card.appendChild(group);

        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    const solicitarRecomendacionConPregunta = (fnPregunta) => {
        fnPregunta();
    };

    // --- Preguntas por modo ---
    const preguntarColor = () => {
        // Paso 1: Mensaje inicial del bot
        appendMessage('Vamos a jugar con colores 😊\nElige uno y te preparo un conjunto.', 'bot');

        // Card con los botones de colores
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

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

                // Mostrar la selección del usuario como burbuja
                appendUserSelection(`Color: ${op.value}`);

                // Paso 2: Preguntar tipo de combinación
                preguntarTipoCombinacion('COLOR');
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
        // Paso 1: Mensaje inicial del bot
        appendMessage('Vamos a elegir una marca 👕\n¿Cuál te apetece usar hoy?', 'bot');

        // Card con los botones de marcas
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

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

                // Mostrar la selección del usuario como burbuja
                appendUserSelection(`Marca: ${op.value}`);

                // Paso 2: Preguntar tipo de combinación
                preguntarTipoCombinacion('MARCA');
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

    // Paso 2: Preguntar tipo de combinación (TODO o COMBINADO)
    const preguntarTipoCombinacion = (criterio) => {
        // Mensaje del bot
        appendMessage('¿Cómo lo quieres?', 'bot');

        // Card con botones
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const opciones = [
            { value: 'TODO', label: criterio === 'COLOR' ? 'Todo del mismo color' : 'Todo de la misma marca' },
            { value: 'COMBINADO', label: 'Combinado con otros' }
        ];

        const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
            estadoModo.tipoCombinacion = op.value;
            marcarSeleccionEnGrupo(groupEl, btn);

            // Mostrar la selección del usuario como burbuja
            appendUserSelection(op.label);

            if (op.value === 'TODO') {
                // Ir directamente a solicitar recomendación
                solicitarRecomendacion();
            } else {
                // Paso 3: Preguntar intensidad
                preguntarIntensidad(criterio);
            }
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    // Paso 3: Preguntar intensidad (SOLO si es COMBINADO)
    const preguntarIntensidad = (criterio) => {
        // Mensaje del bot
        appendMessage('¿Qué intensidad quieres?', 'bot');

        // Card con botones
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const criterioTexto = criterio === 'COLOR' ?
            (estadoModo.colorSeleccionado || 'criterio') :
            (estadoModo.marcaSeleccionada || 'criterio');

        const opciones = [
            { value: 'PROTAGONISTA', label: criterioTexto + ' protagonista' },
            { value: 'TOQUE', label: 'Solo un toque' }
        ];

        const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
            estadoModo.intensidad = op.value;
            marcarSeleccionEnGrupo(groupEl, btn);

            // Mostrar la selección del usuario como burbuja
            appendUserSelection(op.label);

            // Ahora sí, solicitar recomendación
            solicitarRecomendacion();
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
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

            // Mostrar la selección del usuario como burbuja
            appendUserSelection(`Tiempo: ${op.label}`);

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

            // Mostrar la selección del usuario como burbuja
            appendUserSelection(`Ocasión: ${op.label}`);

            solicitarRecomendacion();
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    // --- Modo COMPLETAR: flujo conversacional ---
    const iniciarModoCompletar = () => {
        // Paso 1: Elegir tipo de prenda
        appendMessage('Perfecto 😊\nElige la prenda que quieres completar.', 'bot');

        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const opciones = [
            { value: 'SUPERIOR', label: '👕 Superior' },
            { value: 'INFERIOR', label: '👖 Inferior' },
            { value: 'CALZADO', label: '👟 Calzado' }
        ];

        const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
            marcarSeleccionEnGrupo(groupEl, btn);

            // Mostrar la selección del usuario como burbuja
            appendUserSelection(op.label);

            // Paso 2: Mostrar prendas de ese tipo
            mostrarPrendasParaCompletar(op.value);
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    const mostrarPrendasParaCompletar = (tipoPrenda) => {
        // Mensaje del bot
        appendMessage('Estas son tus prendas.\nElige una y te completo el conjunto.', 'bot');

        // Mostrar indicador de carga
        const typingId = `typing-${Date.now()}`;
        const typingMsg = document.createElement('div');
        typingMsg.classList.add('ai-chat-message', 'ai-chat-message--bot', 'ai-chat-message--typing');
        typingMsg.id = typingId;
        typingMsg.textContent = 'Cargando tus prendas...';
        messagesContainer.appendChild(typingMsg);
        scrollToBottom();

        // Obtener las prendas del usuario según el tipo
        fetch('/api/chat/prendas-por-tipo?tipo=' + tipoPrenda, {
            method: 'GET'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('No se pudieron cargar las prendas');
            }
            return response.json();
        })
        .then(prendas => {
            // Remover el indicador de carga
            if (typingMsg.parentNode) {
                typingMsg.parentNode.removeChild(typingMsg);
            }

            if (!prendas || prendas.length === 0) {
                appendMessage('No tienes prendas de este tipo en tu armario.', 'bot');
                appendMessage('¿Qué te gustaría hacer ahora?', 'bot');

                const card = document.createElement('div');
                card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

                const opciones = [
                    { value: 'ELEGIR_OTRO_TIPO', label: '🔄 Elegir otro tipo' },
                    { value: 'CAMBIAR_MODO', label: '🧭 Cambiar de modo' }
                ];

                const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
                    marcarSeleccionEnGrupo(groupEl, btn);
                    appendUserSelection(op.label);

                    if (op.value === 'ELEGIR_OTRO_TIPO') {
                        iniciarModoCompletar();
                    } else {
                        mostrarMenuModos();
                    }
                });

                card.appendChild(group);
                messagesContainer.appendChild(card);
                scrollToBottom();
                return;
            }

            // Mostrar las prendas en una cuadrícula
            const card = document.createElement('div');
            card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';
            card.style.padding = '1rem';

            const gridContainer = document.createElement('div');
            gridContainer.style.display = 'grid';
            gridContainer.style.gridTemplateColumns = 'repeat(auto-fill, minmax(120px, 1fr))';
            gridContainer.style.gap = '0.8rem';
            gridContainer.style.maxHeight = '400px';
            gridContainer.style.overflowY = 'auto';

            prendas.forEach(prenda => {
                const prendaCard = document.createElement('div');
                prendaCard.style.cursor = 'pointer';
                prendaCard.style.border = '2px solid #e0e0e0';
                prendaCard.style.borderRadius = '8px';
                prendaCard.style.padding = '0.5rem';
                prendaCard.style.textAlign = 'center';
                prendaCard.style.transition = 'all 0.2s';

                prendaCard.addEventListener('mouseenter', () => {
                    prendaCard.style.borderColor = '#007bff';
                    prendaCard.style.transform = 'scale(1.05)';
                });

                prendaCard.addEventListener('mouseleave', () => {
                    prendaCard.style.borderColor = '#e0e0e0';
                    prendaCard.style.transform = 'scale(1)';
                });

                const img = document.createElement('img');
                img.src = prenda.imagenUrl || '/images/placeholder_cloth.png';
                img.alt = prenda.nombre || 'Prenda';
                img.style.width = '100%';
                img.style.height = 'auto';
                img.style.borderRadius = '4px';
                img.style.marginBottom = '0.3rem';
                img.onerror = function() {
                    this.src = '/images/placeholder_cloth.png';
                };

                const nombre = document.createElement('div');
                nombre.textContent = prenda.nombre || 'Sin nombre';
                nombre.style.fontSize = '0.75rem';
                nombre.style.fontWeight = '500';
                nombre.style.marginBottom = '0.2rem';
                nombre.style.overflow = 'hidden';
                nombre.style.textOverflow = 'ellipsis';
                nombre.style.whiteSpace = 'nowrap';

                const detalles = document.createElement('div');
                detalles.style.fontSize = '0.65rem';
                detalles.style.color = '#666';
                const partes = [];
                if (prenda.color) partes.push(prenda.color);
                if (prenda.marca) partes.push(prenda.marca);
                detalles.textContent = partes.join(' · ');

                prendaCard.appendChild(img);
                prendaCard.appendChild(nombre);
                prendaCard.appendChild(detalles);

                prendaCard.addEventListener('click', () => {
                    // Mostrar burbuja de selección
                    appendUserSelection(prenda.nombre || 'Prenda seleccionada');

                    // Fijar la prenda seleccionada según su tipo
                    if (tipoPrenda === 'SUPERIOR') {
                        prendasFijas.superiorId = prenda.id;
                    } else if (tipoPrenda === 'INFERIOR') {
                        prendasFijas.inferiorId = prenda.id;
                    } else if (tipoPrenda === 'CALZADO') {
                        prendasFijas.calzadoId = prenda.id;
                    }

                    // Solicitar recomendación con la prenda fijada
                    solicitarRecomendacion();
                });

                gridContainer.appendChild(prendaCard);
            });

            card.appendChild(gridContainer);
            messagesContainer.appendChild(card);
            scrollToBottom();
        })
        .catch(error => {
            // Remover el indicador de carga
            if (typingMsg.parentNode) {
                typingMsg.parentNode.removeChild(typingMsg);
            }

            appendMessage('No se pudieron cargar las prendas. Inténtalo de nuevo.', 'bot');
            appendMessage('¿Qué te gustaría hacer ahora?', 'bot');

            const card = document.createElement('div');
            card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

            const opciones = [
                { value: 'REINTENTAR', label: '🔄 Reintentar' },
                { value: 'CAMBIAR_MODO', label: '🧭 Cambiar de modo' }
            ];

            const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
                marcarSeleccionEnGrupo(groupEl, btn);
                appendUserSelection(op.label);

                if (op.value === 'REINTENTAR') {
                    mostrarPrendasParaCompletar(tipoPrenda);
                } else {
                    mostrarMenuModos();
                }
            });

            card.appendChild(group);
            messagesContainer.appendChild(card);
            scrollToBottom();
        });
    };

    // --- Render de columnas de prenda, manteniendo botón de fijar ---
    const renderPrendaColumn = (prenda, tipoClave) => {
        const col = document.createElement('div');
        col.className = 'ai-outfit-prenda';
        col.style.textAlign = 'center';

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

    const appendOutfitCard = (recomendacion, opciones = {}) => {
        const fromHistory = opciones.fromHistory === true;

        // Ya no eliminamos la card anterior: queremos histórico tipo chat
        // limpiarRecomendacionActiva();

        // Ocultar barra global mientras hay recomendación activa
        if (barraAccionesGlobal) {
            barraAccionesGlobal.style.display = 'none';
        }
        // Ocultar el saludo cuando se está mostrando una recomendación
        if (greetingEl) {
            greetingEl.classList.add('ai-chat-greeting--hidden');
        }

        // Actualizar estado de "sin repetir": solo añadimos al historial de conjuntosUsados si es una nueva recomendación
        if (!fromHistory && recomendacion && recomendacion.superior && recomendacion.inferior && recomendacion.calzado) {
            const clave = `${recomendacion.superior.id}-${recomendacion.inferior.id}-${recomendacion.calzado.id}`;
            estadoModo.conjuntosUsados.add(clave);
        }

        // Guardar en historial si es una recomendación nueva (no un render desde historial)
        if (!fromHistory && recomendacion) {
            historialRecomendaciones.push(recomendacion);
        }

        // Crear wrapper para la recomendación completa
        const outerWrapper = document.createElement('div');
        outerWrapper.className = 'ai-chat-message-wrapper ai-chat-message-wrapper--bot';
        outerWrapper.style.marginTop = '1rem';
        outerWrapper.style.marginBottom = '1rem';

        const wrapper = document.createElement('div');
        wrapper.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';
        wrapper.style.display = 'flex';
        wrapper.style.flexDirection = 'column';
        wrapper.style.alignItems = 'center';
        wrapper.style.textAlign = 'center';
        wrapper.style.padding = '1rem';
        wrapper.style.borderRadius = '12px';

        const grid = document.createElement('div');
        grid.className = 'ai-outfit-grid';
        grid.style.margin = '0 auto';
        grid.appendChild(renderPrendaColumn(recomendacion.superior, 'superior'));
        grid.appendChild(renderPrendaColumn(recomendacion.inferior, 'inferior'));
        grid.appendChild(renderPrendaColumn(recomendacion.calzado, 'calzado'));
        wrapper.appendChild(grid);

        const explanation = document.createElement('div');
        explanation.className = 'ai-outfit-explicacion';

        // Generar mensaje explicativo según el modo
        if (estadoModo.modo === 'SORPRESA') {
            explanation.textContent = 'Sorpresa inteligente: combinación equilibrada y variada';
        } else if (estadoModo.modo === 'COMPLETAR') {
            explanation.textContent = 'He completado el conjunto a partir de esta prenda.';
        } else if (estadoModo.modo === 'COLOR') {
            if (estadoModo.tipoCombinacion === 'TODO') {
                explanation.textContent = 'Todo ' + (estadoModo.colorSeleccionado || 'del mismo color');
            } else if (estadoModo.tipoCombinacion === 'COMBINADO') {
                if (estadoModo.intensidad === 'PROTAGONISTA') {
                    explanation.textContent = (estadoModo.colorSeleccionado || 'Color') + ' protagonista';
                } else if (estadoModo.intensidad === 'TOQUE') {
                    explanation.textContent = 'Solo un toque de ' + (estadoModo.colorSeleccionado || 'color');
                } else {
                    explanation.textContent = (estadoModo.colorSeleccionado || 'Color') + ' combinado con otros colores';
                }
            } else {
                explanation.textContent = recomendacion.mensaje || '';
            }
        } else if (estadoModo.modo === 'MARCA') {
            if (estadoModo.tipoCombinacion === 'TODO') {
                explanation.textContent = 'Todo de ' + (estadoModo.marcaSeleccionada || 'la misma marca');
            } else if (estadoModo.tipoCombinacion === 'COMBINADO') {
                if (estadoModo.intensidad === 'PROTAGONISTA') {
                    explanation.textContent = 'Marca ' + (estadoModo.marcaSeleccionada || '') + ' protagonista';
                } else if (estadoModo.intensidad === 'TOQUE') {
                    explanation.textContent = 'Solo un toque de ' + (estadoModo.marcaSeleccionada || 'marca');
                } else {
                    explanation.textContent = (estadoModo.marcaSeleccionada || 'Marca') + ' combinada con otras marcas';
                }
            } else {
                explanation.textContent = recomendacion.mensaje || '';
            }
        } else {
            explanation.textContent = recomendacion.mensaje || '';
        }
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
        actions.style.justifyContent = 'center';

        // Botón lateral para volver rápidamente al menú de modos
        const modosInlineBtn = document.createElement('button');
        modosInlineBtn.type = 'button';
        modosInlineBtn.className = 'ai-outfit-btn ai-outfit-btn--ghost';
        modosInlineBtn.textContent = 'Modos';
        modosInlineBtn.addEventListener('click', function () {
            // Mostrar burbuja del usuario
            appendUserSelection('Cambiar de modo');
            mostrarMenuModos();
        });
        actions.appendChild(modosInlineBtn);

        const otraBtn = document.createElement('button');
        otraBtn.type = 'button';
        otraBtn.className = 'ai-outfit-btn';
        otraBtn.textContent = 'Otra sugerencia';
        otraBtn.addEventListener('click', function () {
            if (estadoModo.cargandoRecomendacion) return;

            // Mostrar burbuja del usuario
            appendUserSelection('Otra sugerencia');

            estadoModo.cargandoRecomendacion = true;
            otraBtn.disabled = true;
            solicitarRecomendacion().finally(() => {
                estadoModo.cargandoRecomendacion = false;
                otraBtn.disabled = false;
            });
        });

        const guardarBtn = document.createElement('button');
        guardarBtn.type = 'button';
        guardarBtn.className = 'ai-outfit-btn ai-outfit-btn--primary';

        // Verificar si esta recomendación ya fue guardada
        const yaGuardado = recomendacion.yaGuardado === true;

        if (yaGuardado) {
            guardarBtn.textContent = 'Ya guardado ✓';
            guardarBtn.disabled = true;
        } else {
            guardarBtn.textContent = 'Guardar conjunto';
        }

        guardarBtn.addEventListener('click', function () {
            if (recomendacion.yaGuardado) return; // Prevenir guardado múltiple

            // Mostrar burbuja del usuario
            appendUserSelection('Guardar conjunto');

            mostrarFormularioGuardado(wrapper, recomendacion, guardarBtn);
        });

        if (Array.isArray(recomendacion.faltantes) && recomendacion.faltantes.length > 0) {
            guardarBtn.disabled = true;
        }

        actions.appendChild(otraBtn);
        actions.appendChild(guardarBtn);
        wrapper.appendChild(actions);

        // Añadir el wrapper al outer wrapper
        outerWrapper.appendChild(wrapper);

        // El menú de modos NO se muestra dentro de cada card para mantener el chat limpio.
        // El usuario puede cambiar de modo usando:
        // - El botón "Modos" de la cabecera.
        // - El botón "Modos" lateral de cada card.

        messagesContainer.appendChild(outerWrapper);
        ultimaCardRecomendacion = wrapper;
        estadoModo.hayRecomendacionActiva = true;
        estadoModo.cargandoRecomendacion = false;
        scrollToBottom();
    };

    const mostrarFormularioGuardado = (cardRecomendacion, recomendacion, guardarBtn) => {
        // Verificar si ya está guardado
        if (recomendacion.yaGuardado) {
            appendMessage('Este conjunto ya ha sido guardado.', 'bot');
            return;
        }

        if (!recomendacion || !recomendacion.superior || !recomendacion.inferior || !recomendacion.calzado) {
            appendMessage('No se puede guardar un conjunto incompleto.', 'bot');
            return;
        }

        // Preguntar cómo quiere nombrar el conjunto
        appendMessage('¿Cómo quieres nombrar este conjunto?', 'bot');

        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const opciones = [
            { value: 'MANUAL', label: 'Yo elijo el nombre' },
            { value: 'AUTO', label: 'Que lo nombre la IA' }
        ];

        const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
            marcarSeleccionEnGrupo(groupEl, btn);

            // Mostrar burbuja del usuario
            appendUserSelection(op.label);

            if (op.value === 'MANUAL') {
                mostrarFormularioNombreManual(recomendacion, guardarBtn);
            } else {
                mostrarFormularioNotaOpcional(recomendacion, guardarBtn, null);
            }
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
        scrollToBottom();
    };

    const mostrarFormularioNombreManual = (recomendacion, guardarBtn) => {
        appendMessage('Escribe un nombre para el conjunto:', 'bot');

        const formCard = document.createElement('div');
        formCard.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';
        formCard.style.padding = '1rem';

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'ai-chat-input';
        input.placeholder = 'Ej: Look casual viernes';
        input.style.width = '100%';
        input.style.padding = '0.6rem';
        input.style.border = '1px solid #ddd';
        input.style.borderRadius = '8px';
        input.style.fontSize = '0.9rem';
        input.style.marginBottom = '0.6rem';

        const btnContainer = document.createElement('div');
        btnContainer.style.display = 'flex';
        btnContainer.style.gap = '0.5rem';
        btnContainer.style.justifyContent = 'flex-end';

        const confirmarBtn = document.createElement('button');
        confirmarBtn.type = 'button';
        confirmarBtn.className = 'ai-outfit-btn ai-outfit-btn--primary';
        confirmarBtn.textContent = 'Continuar';
        confirmarBtn.addEventListener('click', function () {
            const nombre = input.value.trim();
            if (!nombre) {
                input.style.borderColor = '#ff4444';
                input.placeholder = 'El nombre es obligatorio';
                return;
            }

            // Mostrar el nombre elegido como burbuja
            appendUserSelection('Nombre: ' + nombre);

            // Deshabilitar el formulario
            input.disabled = true;
            confirmarBtn.disabled = true;

            // Continuar con la nota opcional
            mostrarFormularioNotaOpcional(recomendacion, guardarBtn, nombre);
        });

        btnContainer.appendChild(confirmarBtn);
        formCard.appendChild(input);
        formCard.appendChild(btnContainer);
        messagesContainer.appendChild(formCard);
        scrollToBottom();

        // Focus en el input
        setTimeout(() => input.focus(), 100);
    };

    const mostrarFormularioNotaOpcional = (recomendacion, guardarBtn, nombreManual) => {
        appendMessage('¿Quieres añadir alguna nota? (opcional)', 'bot');

        const formCard = document.createElement('div');
        formCard.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';
        formCard.style.padding = '1rem';

        const textarea = document.createElement('textarea');
        textarea.className = 'ai-chat-input';
        textarea.placeholder = 'Ej: Para días soleados de primavera';
        textarea.style.width = '100%';
        textarea.style.padding = '0.6rem';
        textarea.style.border = '1px solid #ddd';
        textarea.style.borderRadius = '8px';
        textarea.style.fontSize = '0.9rem';
        textarea.style.marginBottom = '0.6rem';
        textarea.style.minHeight = '60px';
        textarea.style.resize = 'vertical';

        const btnContainer = document.createElement('div');
        btnContainer.style.display = 'flex';
        btnContainer.style.gap = '0.5rem';
        btnContainer.style.justifyContent = 'flex-end';

        const omitirBtn = document.createElement('button');
        omitirBtn.type = 'button';
        omitirBtn.className = 'ai-outfit-btn ai-outfit-btn--ghost';
        omitirBtn.textContent = 'Sin nota';
        omitirBtn.addEventListener('click', function () {
            appendUserSelection('Sin nota');
            textarea.disabled = true;
            omitirBtn.disabled = true;
            guardarBtn.disabled = true;
            guardarConjuntoFinal(recomendacion, guardarBtn, nombreManual, null);
        });

        const guardarBtnFinal = document.createElement('button');
        guardarBtnFinal.type = 'button';
        guardarBtnFinal.className = 'ai-outfit-btn ai-outfit-btn--primary';
        guardarBtnFinal.textContent = 'Guardar conjunto';
        guardarBtnFinal.addEventListener('click', function () {
            const nota = textarea.value.trim();
            if (nota) {
                appendUserSelection('Nota: ' + nota);
            } else {
                appendUserSelection('Sin nota');
            }
            textarea.disabled = true;
            omitirBtn.disabled = true;
            guardarBtnFinal.disabled = true;
            guardarConjuntoFinal(recomendacion, guardarBtn, nombreManual, nota || null);
        });

        btnContainer.appendChild(omitirBtn);
        btnContainer.appendChild(guardarBtnFinal);
        formCard.appendChild(textarea);
        formCard.appendChild(btnContainer);
        messagesContainer.appendChild(formCard);
        scrollToBottom();
    };

    const guardarConjuntoFinal = (recomendacion, guardarBtn, nombreManual, nota) => {
        // Determinar el nombre final
        let nombreFinal;
        if (nombreManual) {
            nombreFinal = nombreManual;
        } else {
            // Generar nombre automático con IA
            const timestamp = new Date().toLocaleString('es-ES', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
            nombreFinal = 'Conjunto ' + timestamp;
        }

        // Preparar el body para guardar
        const bodyGuardar = {
            nombre: nombreFinal,
            prendaSuperiorId: recomendacion.superior.id,
            prendaInferiorId: recomendacion.inferior.id,
            prendaCalzadoId: recomendacion.calzado.id,
            notas: nota || null
        };

        // Llamar al endpoint correcto de guardado
        fetch('/api/chat/guardar-conjunto', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bodyGuardar)
        })
            .then(function (response) {
                if (!response.ok) {
                    // Intentar leer el mensaje de error del backend
                    return response.json().then(data => {
                        throw new Error(data.mensaje || 'No se pudo guardar el conjunto');
                    }).catch(() => {
                        throw new Error('No se pudo guardar el conjunto');
                    });
                }
                return response.json();
            })
            .then(function (data) {
                // Verificar si el guardado fue exitoso
                if (data && data.exito === false) {
                    appendMessage(data.mensaje || 'No se pudo guardar el conjunto.', 'bot');
                    return;
                }

                // Marcar la recomendación como guardada
                recomendacion.yaGuardado = true;

                // Actualizar el botón visualmente
                if (guardarBtn) {
                    guardarBtn.textContent = 'Ya guardado ✓';
                    guardarBtn.disabled = true;
                }

                // Mensaje de éxito
                appendMessage('Conjunto guardado correctamente 😊', 'bot');
                appendMessage('¿Necesitas algo más?', 'bot');

                // Mostrar botones de modos
                const card = document.createElement('div');
                card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

                const modos = [
                    { value: 'SORPRESA', label: '🎲 Conjunto sorpresa' },
                    { value: 'COLOR', label: '🎨 Por color' },
                    { value: 'MARCA', label: '🏷️ Por marca' },
                    { value: 'TIEMPO', label: '🌤️ Por tiempo' },
                    { value: 'OCASION', label: '🎯 Por ocasión' },
                    { value: 'COMPLETAR', label: '🧩 Completar esta prenda' }
                ];

                const group = crearGrupoBotones(modos, (op, groupEl, btn) => {
                    marcarSeleccionEnGrupo(groupEl, btn);

                    // Mostrar burbuja del usuario
                    appendUserSelection(op.label);

                    resetModo();
                    estadoModo.modo = op.value;

                    if (op.value === 'SORPRESA') {
                        solicitarRecomendacion();
                    } else if (op.value === 'COLOR') {
                        solicitarRecomendacionConPregunta(preguntarColor);
                    } else if (op.value === 'MARCA') {
                        solicitarRecomendacionConPregunta(preguntarMarca);
                    } else if (op.value === 'TIEMPO') {
                        solicitarRecomendacionConPregunta(preguntarTiempo);
                    } else if (op.value === 'OCASION') {
                        solicitarRecomendacionConPregunta(preguntarOcasion);
                    } else if (op.value === 'COMPLETAR') {
                        iniciarModoCompletar();
                    }
                });

                card.appendChild(group);
                messagesContainer.appendChild(card);
                scrollToBottom();
            })
            .catch(function (err) {
                const mensaje = err.message || 'No se pudo guardar el conjunto. Inténtalo de nuevo.';
                appendMessage(mensaje, 'bot');
            });
    };

    const mostrarErrorRecomendacion = (mensaje) => {
        // Mostrar mensaje de error
        appendMessage(mensaje, 'bot');

        // Caso específico para modo COMPLETAR
        if (estadoModo.modo === 'COMPLETAR') {
            appendMessage('¿Qué te gustaría hacer ahora?', 'bot');

            const card = document.createElement('div');
            card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

            const opciones = [
                { value: 'ELEGIR_OTRA_PRENDA', label: '🔄 Elegir otra prenda' },
                { value: 'CAMBIAR_MODO', label: '🧭 Cambiar de modo' }
            ];

            const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
                marcarSeleccionEnGrupo(groupEl, btn);
                appendUserSelection(op.label);

                if (op.value === 'ELEGIR_OTRA_PRENDA') {
                    iniciarModoCompletar();
                } else {
                    mostrarMenuModos();
                }
            });

            card.appendChild(group);
            messagesContainer.appendChild(card);
            scrollToBottom();
            return;
        }

        // Determinar si es un caso de "TODO no posible" en modos COLOR o MARCA
        const esModoColorOMarca = (estadoModo.modo === 'COLOR' || estadoModo.modo === 'MARCA');
        const esTodoNoDisponible = estadoModo.tipoCombinacion === 'TODO';

        if (esModoColorOMarca && esTodoNoDisponible) {
            // Caso límite 1: "Todo del mismo criterio" no es posible
            appendMessage('¿Qué te gustaría hacer ahora?', 'bot');

            const card = document.createElement('div');
            card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

            const criterio = estadoModo.modo === 'COLOR' ? 'color' : 'marca';

            const opciones = [
                { value: 'PROBAR_COMBINADO', label: 'Probar combinado' },
                { value: 'ELEGIR_OTRO', label: 'Elegir otro ' + criterio },
                { value: 'CAMBIAR_MODO', label: 'Cambiar de modo' }
            ];

            const group = crearGrupoBotones(opciones, (op, groupEl, btn) => {
                marcarSeleccionEnGrupo(groupEl, btn);

                // Mostrar burbuja del usuario
                appendUserSelection(op.label);

                if (op.value === 'PROBAR_COMBINADO') {
                    // Cambiar a combinado y preguntar intensidad
                    estadoModo.tipoCombinacion = 'COMBINADO';
                    preguntarIntensidad(estadoModo.modo);
                } else if (op.value === 'ELEGIR_OTRO') {
                    // Volver a preguntar color o marca
                    resetModo();
                    estadoModo.modo = estadoModo.modo === 'COLOR' ? 'COLOR' : 'MARCA';
                    if (estadoModo.modo === 'COLOR') {
                        preguntarColor();
                    } else {
                        preguntarMarca();
                    }
                } else if (op.value === 'CAMBIAR_MODO') {
                    // Mostrar menú de modos
                    mostrarMenuModos();
                }
            });

            card.appendChild(group);
            messagesContainer.appendChild(card);
            scrollToBottom();
        } else {
            // Otros errores: mostrar menú de modos completo
            appendMessage('¿Qué te gustaría hacer ahora?', 'bot');

            const card = document.createElement('div');
            card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

            const modos = [
                { value: 'SORPRESA', label: '🎲 Conjunto sorpresa' },
                { value: 'COLOR', label: '🎨 Por color' },
                { value: 'MARCA', label: '🏷️ Por marca' },
                { value: 'TIEMPO', label: '🌤️ Por tiempo' },
                { value: 'OCASION', label: '🎯 Por ocasión' },
                { value: 'COMPLETAR', label: '🧩 Completar esta prenda' }
            ];

            const group = crearGrupoBotones(modos, (op, groupEl, btn) => {
                marcarSeleccionEnGrupo(groupEl, btn);

                // Mostrar burbuja del usuario
                appendUserSelection(op.label);

                resetModo();
                estadoModo.modo = op.value;

                if (op.value === 'SORPRESA') {
                    solicitarRecomendacion();
                } else if (op.value === 'COLOR') {
                    solicitarRecomendacionConPregunta(preguntarColor);
                } else if (op.value === 'MARCA') {
                    solicitarRecomendacionConPregunta(preguntarMarca);
                } else if (op.value === 'TIEMPO') {
                    solicitarRecomendacionConPregunta(preguntarTiempo);
                } else if (op.value === 'OCASION') {
                    solicitarRecomendacionConPregunta(preguntarOcasion);
                } else if (op.value === 'COMPLETAR') {
                    iniciarModoCompletar();
                }
            });

            card.appendChild(group);
            messagesContainer.appendChild(card);
            scrollToBottom();
        }
    };

    const mostrarMenuModosSecundario = () => {
        const card = document.createElement('div');
        card.className = 'ai-chat-message ai-chat-message--bot ai-chat-message--outfit';

        const titulo = document.createElement('div');
        titulo.className = 'ai-outfit-explicacion';
        titulo.textContent = '¿Necesitas algo más?';
        card.appendChild(titulo);

        const modos = [
            { value: 'SORPRESA', label: '🎲 Conjunto sorpresa' },
            { value: 'COLOR', label: '🎨 Por color' },
            { value: 'MARCA', label: '🏷️ Por marca' },
            { value: 'TIEMPO', label: '🌤️ Por tiempo' },
            { value: 'OCASION', label: '🎯 Por ocasión' },
            { value: 'COMPLETAR', label: '🧩 Completar esta prenda' }
        ];

        const group = crearGrupoBotones(modos, (op, groupEl, btn) => {
            marcarSeleccionEnGrupo(groupEl, btn);
            resetModo();
            estadoModo.modo = op.value;
            if (op.value === 'SORPRESA') {
                solicitarRecomendacion();
            } else if (op.value === 'COLOR') {
                solicitarRecomendacionConPregunta(preguntarColor);
            } else if (op.value === 'MARCA') {
                solicitarRecomendacionConPregunta(preguntarMarca);
            } else if (op.value === 'TIEMPO') {
                solicitarRecomendacionConPregunta(preguntarTiempo);
            } else if (op.value === 'OCASION') {
                solicitarRecomendacionConPregunta(preguntarOcasion);
            } else if (op.value === 'COMPLETAR') {
                iniciarModoCompletar();
            }
        });

        card.appendChild(group);
        messagesContainer.appendChild(card);
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
            conjuntosUsados: Array.from(estadoModo.conjuntosUsados),
            tipoCombinacion: estadoModo.tipoCombinacion,
            intensidad: estadoModo.intensidad
        };

        return fetch('/api/chat/recomendar-conjunto', {
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
                if (!data || (!data.superior && !data.inferior && !data.calzado)) {
                    mostrarErrorRecomendacion('No hay más combinaciones posibles con tus prendas actuales.');
                    return;
                }

                const yaUsado =
                    data.superior && data.inferior && data.calzado &&
                    estadoModo.conjuntosUsados.has(`${data.superior.id}-${data.inferior.id}-${data.calzado.id}`);

                if (yaUsado) {
                    mostrarErrorRecomendacion('No hay más combinaciones posibles con tus prendas actuales.');
                    return;
                }

                appendOutfitCard(data);
            })
            .catch(function (err) {
                if (typingMsg.parentNode) {
                    typingMsg.parentNode.removeChild(typingMsg);
                }
                mostrarErrorRecomendacion(err.message || 'No se ha podido generar un nuevo conjunto.');
            });
    };

    const crearBotonRecomendar = () => {
        if (document.getElementById('ai-chat-recomendar-btn')) {
            barraAccionesGlobal = document.getElementById('ai-chat-recomendar-btn').closest('div');
            return;
        }
        const body = windowEl.querySelector('.ai-chat-body');
        if (!body) return;

        const actions = document.createElement('div');
        actions.style.marginTop = '0.5rem';
        actions.style.display = 'flex';
        actions.style.justifyContent = 'center'; // centrado horizontal
        actions.style.alignItems = 'center';
        actions.style.gap = '0.5rem';

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'ai-chat-recomendar-btn';
        btn.className = 'ai-chat-recomendar-btn';
        btn.textContent = 'Recomiéndame…';
        btn.addEventListener('click', function () {
            mostrarMenuModos();
        });

        actions.appendChild(btn);
        body.appendChild(actions);
        barraAccionesGlobal = actions;
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
            // Al cerrar con "X": resetear completamente la sesión del chat
            mostrarPantallaInicio();
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
