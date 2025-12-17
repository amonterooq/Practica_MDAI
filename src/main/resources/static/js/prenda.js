// --- Utilidades de categorías/tallas en alta de prenda ---
function mostrarCategorias() {
    const divCatSuperior = document.getElementById('divCatSuperior');
    const divCatInferior = document.getElementById('divCatInferior');
    const divCatCalzado = document.getElementById('divCatCalzado');
    const divTalla = document.getElementById('divTalla');
    const tipoSelect = document.getElementById('tipoPrendaSelect');

    if (!divCatSuperior || !divCatInferior || !divCatCalzado || !divTalla || !tipoSelect) {
        console.warn('[armario] Elementos de tipo/categoría/talla no encontrados');
        return;
    }

    divCatSuperior.classList.add('hidden');
    divCatInferior.classList.add('hidden');
    divCatCalzado.classList.add('hidden');
    divTalla.classList.add('hidden');

    const tipo = tipoSelect.value;

    const selSup = divCatSuperior.querySelector('select');
    const selInf = divCatInferior.querySelector('select');
    const selCal = divCatCalzado.querySelector('select');
    if (selSup) selSup.value = '';
    if (selInf) selInf.value = '';
    if (selCal) selCal.value = '';

    if (tipo === 'superior') {
        divCatSuperior.classList.remove('hidden');
    } else if (tipo === 'inferior') {
        divCatInferior.classList.remove('hidden');
    } else if (tipo === 'calzado') {
        divCatCalzado.classList.remove('hidden');
    }

    if (tipo) {
        divTalla.classList.remove('hidden');
    }

    rellenarTallas(tipo);
}

function rellenarTallas(tipo) {
    const select = document.getElementById('tallaSelect');
    if (!select) return;

    while (select.firstChild) {
        select.removeChild(select.firstChild);
    }

    const optDefault = document.createElement('option');
    optDefault.value = '';
    optDefault.selected = true;
    optDefault.textContent = '-- Seleccionar --';
    select.appendChild(optDefault);

    let tallas = [];
    if (tipo === 'superior') {
        tallas = ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL', 'XXXXL'];
    } else if (tipo === 'inferior') {
        tallas = ['32', '34', '36', '38', '40', '42', '44', '46', '48', '50'];
    } else if (tipo === 'calzado') {
        tallas = ['34', '35', '36', '37', '38', '39', '40', '41', '42', '43', '44', '45', '46'];
    }

    tallas.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t;
        opt.textContent = t;
        select.appendChild(opt);
    });
}

// --- Marca / color en alta ---
function actualizarMarcaFinal() {
    const select = document.getElementById('marcaSelect');
    const inputOtra = document.getElementById('marcaOtraInput');
    const hiddenFinal = document.getElementById('marcaFinal');
    if (!select || !inputOtra || !hiddenFinal) return;

    const valorSelect = select.value;
    const valorOtra = inputOtra.value.trim();
    hiddenFinal.value = (valorSelect === 'Otro') ? (valorOtra || '') : (valorSelect || '');
}

function actualizarColorFinal() {
    const select = document.getElementById('colorSelect');
    const inputOtro = document.getElementById('colorOtroInput');
    const hiddenFinal = document.getElementById('colorFinal');
    if (!select || !inputOtro || !hiddenFinal) return;

    const valorSelect = select.value;
    const valorOtro = inputOtro.value.trim();
    hiddenFinal.value = (valorSelect === 'Otro') ? (valorOtro || '') : (valorSelect || '');
}

// --- Marca / color en modal de edición ---
function actualizarMarcaFinalEdicion() {
    const select = document.getElementById('editMarcaSelect');
    const hiddenFinal = document.getElementById('editMarca');
    if (!select || !hiddenFinal) return;

    // Para marca, solo usamos el valor del select (sin campo de texto)
    hiddenFinal.value = select.value || '';
}

function actualizarColorFinalEdicion() {
    const select = document.getElementById('editColorSelect');
    const inputOtro = document.getElementById('editColorOtraInput');
    const hiddenFinal = document.getElementById('editColor');
    if (!select || !inputOtro || !hiddenFinal) return;

    const valorSelect = select.value;
    const valorOtro = inputOtro.value.trim();
    hiddenFinal.value = (valorSelect === 'Otro') ? (valorOtro || '') : (valorSelect || '');
}

// --- Tallas en modal de edición ---
function rellenarTallasEdicion(tipo, tallaActual) {
    const select = document.getElementById('editTalla');
    if (!select) return;

    while (select.firstChild) {
        select.removeChild(select.firstChild);
    }

    const optDefault = document.createElement('option');
    optDefault.value = '';
    optDefault.textContent = '-- Seleccionar --';
    select.appendChild(optDefault);

    let tallas = [];
    if (tipo === 'superior') {
        tallas = ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL', 'XXXXL'];
    } else if (tipo === 'inferior') {
        tallas = ['32', '34', '36', '38', '40', '42', '44', '46', '48', '50'];
    } else if (tipo === 'calzado') {
        tallas = ['34', '35', '36', '37', '38', '39', '40', '41', '42', '43', '44', '45', '46'];
    }

    tallas.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t;
        opt.textContent = t;
        if (tallaActual && tallaActual === t) {
            opt.selected = true;
        }
        select.appendChild(opt);
    });

    if (!tallaActual || !tallas.includes(tallaActual)) {
        optDefault.selected = true;
    }
}

// --- Filtros dinámicos ---
function mostrarCategoriasYTallasFiltro() {
    const divSup = document.getElementById('divFiltroCatSuperior');
    const divInf = document.getElementById('divFiltroCatInferior');
    const divCal = document.getElementById('divFiltroCatCalzado');
    const divTalla = document.getElementById('divFiltroTalla');
    const tipoSel = document.getElementById('filtroTipoSelect');
    if (!divSup || !divInf || !divCal || !divTalla || !tipoSel) return;

    divSup.classList.add('hidden');
    divInf.classList.add('hidden');
    divCal.classList.add('hidden');
    divTalla.classList.add('hidden');

    const tipo = tipoSel.value;

    const selSupFiltro = divSup.querySelector('select');
    const selInfFiltro = divInf.querySelector('select');
    const selCalFiltro = divCal.querySelector('select');
    if (selSupFiltro) selSupFiltro.value = '';
    if (selInfFiltro) selInfFiltro.value = '';
    if (selCalFiltro) selCalFiltro.value = '';

    if (tipo === 'superior') divSup.classList.remove('hidden');
    else if (tipo === 'inferior') divInf.classList.remove('hidden');
    else if (tipo === 'calzado') divCal.classList.remove('hidden');

    if (tipo) {
        divTalla.classList.remove('hidden');
        rellenarTallasFiltro(tipo);
    }
}

function rellenarTallasFiltro(tipo) {
    const select = document.getElementById('filtroTallaSelect');
    if (!select) return;

    const tallaActual = select.getAttribute('data-talla-seleccionada') || '';
    while (select.firstChild) {
        select.removeChild(select.firstChild);
    }

    const optDefault = document.createElement('option');
    optDefault.value = '';
    optDefault.textContent = 'Talla';
    select.appendChild(optDefault);

    let tallas = [];
    if (tipo === 'superior') {
        tallas = ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL', 'XXXXL'];
    } else if (tipo === 'inferior') {
        tallas = ['32', '34', '36', '38', '40', '42', '44', '46', '48', '50'];
    } else if (tipo === 'calzado') {
        tallas = ['34', '35', '36', '37', '38', '39', '40', '41', '42', '43', '44', '45', '46'];
    }

    tallas.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t;
        opt.textContent = t;
        if (tallaActual && t === tallaActual) {
            opt.selected = true;
        }
        select.appendChild(opt);
    });
}

function mostrarCategoriasYTallasFiltroSinLimpiar() {
    const divSup = document.getElementById('divFiltroCatSuperior');
    const divInf = document.getElementById('divFiltroCatInferior');
    const divCal = document.getElementById('divFiltroCatCalzado');
    const divTalla = document.getElementById('divFiltroTalla');
    const tipoSel = document.getElementById('filtroTipoSelect');
    if (!divSup || !divInf || !divCal || !divTalla || !tipoSel) return;

    divSup.classList.add('hidden');
    divInf.classList.add('hidden');
    divCal.classList.add('hidden');
    divTalla.classList.add('hidden');

    const tipo = tipoSel.value;

    if (tipo === 'superior') divSup.classList.remove('hidden');
    else if (tipo === 'inferior') divInf.classList.remove('hidden');
    else if (tipo === 'calzado') divCal.classList.remove('hidden');

    if (tipo) {
        divTalla.classList.remove('hidden');
        rellenarTallasFiltro(tipo);
    }
}

// Función auxiliar para generar la imagen 800x800, reutilizada por el recorte
function generarImagen800(img, recorte, imagenRecortadaInput) {
    if (!img || !imagenRecortadaInput) return;
    const outCanvas = document.createElement('canvas');
    const outCtx = outCanvas.getContext('2d');
    outCanvas.width = 800;
    outCanvas.height = 800;

    outCtx.fillStyle = '#ffffff';
    outCtx.fillRect(0, 0, 800, 800);

    let sx = 0, sy = 0, sw = img.width, sh = img.height;
    if (recorte) {
        sx = Math.max(0, recorte.x);
        sy = Math.max(0, recorte.y);
        sw = Math.min(recorte.w, img.width - sx);
        sh = Math.min(recorte.h, img.height - sy);
    }

    const scale = Math.min(800 / sw, 800 / sh);
    const dw = sw * scale;
    const dh = sh * scale;
    const dx = (800 - dw) / 2;
    const dy = (800 - dh) / 2;

    outCtx.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh);
    const dataUrl = outCanvas.toDataURL('image/jpeg', 0.9);
    imagenRecortadaInput.value = dataUrl;
}

// --- Inicialización cuando el DOM está listo ---
document.addEventListener('DOMContentLoaded', function () {
    // Inicializar selección de tipo/categoría/talla en alta
    mostrarCategorias();

    // Alta: color (con opción de escribir si selecciona "Otro")
    const selectColor = document.getElementById('colorSelect');
    const inputColorOtro = document.getElementById('colorOtroInput');
    if (selectColor && inputColorOtro) {
        selectColor.addEventListener('change', function () {
            if (this.value === 'Otro') {
                inputColorOtro.style.display = 'block';
            } else {
                inputColorOtro.style.display = 'none';
                inputColorOtro.value = '';
            }
            actualizarColorFinal();
        });
        inputColorOtro.addEventListener('input', actualizarColorFinal);
    }

    // Edición: marca (solo select, sin campo de texto)
    const selectMarcaEdit = document.getElementById('editMarcaSelect');
    if (selectMarcaEdit) {
        selectMarcaEdit.addEventListener('change', function () {
            actualizarMarcaFinalEdicion();
        });
    }

    // Edición: color
    const selectColorEdit = document.getElementById('editColorSelect');
    const inputColorOtraEdit = document.getElementById('editColorOtraInput');
    if (selectColorEdit && inputColorOtraEdit) {
        selectColorEdit.addEventListener('change', function () {
            if (this.value === 'Otro') {
                inputColorOtraEdit.style.display = 'block';
            } else {
                inputColorOtraEdit.style.display = 'none';
                inputColorOtraEdit.value = '';
            }
            actualizarColorFinalEdicion();
        });
        inputColorOtraEdit.addEventListener('input', actualizarColorFinalEdicion);
    }

    // --- Modal de edición de prenda ---
    const editModalOverlay = document.getElementById('editModalOverlay');
    const editPrendaForm = document.getElementById('editPrendaForm');
    const closeEditModalBtn = document.getElementById('closeEditModalBtn');
    const editModalImage = document.getElementById('editModalImage');
    const editIdInput = document.getElementById('editId');
    const editTipoPrendaInput = document.getElementById('editTipoPrenda');
    const editNombreInput = document.getElementById('editNombre');
    const editMarcaHidden = document.getElementById('editMarca');
    const editMarcaSelectEl = document.getElementById('editMarcaSelect');
    const editTallaSelect = document.getElementById('editTalla');
    const editColorHidden = document.getElementById('editColor');
    const editColorSelectEl = document.getElementById('editColorSelect');
    const editModalError = document.getElementById('editModalError');

    function cerrarModalEdicion() {
        if (!editModalOverlay) return;
        editModalOverlay.style.display = 'none';
        if (editModalError) {
            editModalError.textContent = '';
            editModalError.style.display = 'none';
        }
    }

    function abrirModalEdicion(card) {
        if (!editModalOverlay || !card) return;

        const idEl = card.querySelector('.card-id');
        const nombreEl = card.querySelector('.card-nombre');
        const marcaEl = card.querySelector('.card-marca');
        const tallaEl = card.querySelector('.card-talla');
        const colorEl = card.querySelector('.card-color');
        const badgeSup = card.querySelector('.card-badge-superior');
        const badgeInf = card.querySelector('.card-badge-inferior');
        const badgeCal = card.querySelector('.card-badge-calzado');
        const imgEl = card.querySelector('.card-img img');

        const id = idEl ? idEl.textContent.trim() : '';
        const nombre = nombreEl ? nombreEl.textContent.trim() : '';
        const marca = marcaEl ? marcaEl.textContent.trim() : '';
        const talla = tallaEl ? tallaEl.textContent.trim() : '';
        const color = colorEl ? colorEl.textContent.trim() : '';

        let tipoPrenda = '';
        if (badgeSup) tipoPrenda = 'superior';
        else if (badgeInf) tipoPrenda = 'inferior';
        else if (badgeCal) tipoPrenda = 'calzado';

        if (editIdInput) editIdInput.value = id;
        if (editTipoPrendaInput) editTipoPrendaInput.value = tipoPrenda;
        if (editNombreInput) editNombreInput.value = nombre;

        // MARCA: Solo seleccionar de la lista, sin campo de texto adicional
        const editMarcaOtraInput = document.getElementById('editMarcaOtraInput');
        if (editMarcaSelectEl) {
            // Siempre ocultar el campo de texto para marca
            if (editMarcaOtraInput) {
                editMarcaOtraInput.style.display = 'none';
                editMarcaOtraInput.value = '';
            }

            let found = false;
            Array.from(editMarcaSelectEl.options).forEach(opt => {
                if (opt.value === marca) {
                    opt.selected = true;
                    found = true;
                }
            });
            // Si no está en la lista, seleccionar "Otra" (sin mostrar campo de texto)
            if (!found) {
                editMarcaSelectEl.value = 'Otra';
            }
            actualizarMarcaFinalEdicion();
        }

        rellenarTallasEdicion(tipoPrenda, talla);

        // COLOR: Mostrar campo de texto solo si el color no está en la lista
        const editColorOtraInput = document.getElementById('editColorOtraInput');
        if (editColorSelectEl) {
            // Primero ocultar el campo de texto
            if (editColorOtraInput) {
                editColorOtraInput.style.display = 'none';
                editColorOtraInput.value = '';
            }

            let foundColor = false;
            Array.from(editColorSelectEl.options).forEach(opt => {
                if (opt.value === color) {
                    opt.selected = true;
                    foundColor = true;
                }
            });

            // Si el color NO está en la lista, es un color personalizado
            if (!foundColor) {
                editColorSelectEl.value = 'Otro';
                if (editColorOtraInput) {
                    editColorOtraInput.style.display = 'block';
                    editColorOtraInput.value = color;
                }
            }
            actualizarColorFinalEdicion();
        }

        if (editModalImage) {
            if (imgEl && imgEl.getAttribute('src')) {
                editModalImage.src = imgEl.getAttribute('src');
            } else {
                editModalImage.src = '/images/placeholder_cloth.png';
            }
        }

        editModalOverlay.style.display = 'flex';
    }

    // Clic en tarjeta para abrir modal de edición (excepto botón borrar)
    document.addEventListener('click', function (e) {
        const card = e.target.closest('.garment-card');
        if (!card) return;
        if (e.target.closest('.delete-btn')) return;
        try {
            abrirModalEdicion(card);
        } catch (err) {
            console.error('[armario] Error al abrir modal de edición', err);
        }
    });

    if (closeEditModalBtn && editModalOverlay) {
        closeEditModalBtn.addEventListener('click', cerrarModalEdicion);
        editModalOverlay.addEventListener('click', function (e) {
            if (e.target === editModalOverlay) {
                cerrarModalEdicion();
            }
        });
    }

    if (editPrendaForm) {
        editPrendaForm.addEventListener('submit', function (e) {
            const nombre = editNombreInput ? editNombreInput.value.trim() : '';
            const marca = editMarcaHidden ? editMarcaHidden.value.trim() : '';
            const talla = editTallaSelect ? (editTallaSelect.value || '').trim() : '';
            const color = editColorHidden ? editColorHidden.value.trim() : '';

            if (!nombre || !marca || !talla || !color) {
                e.preventDefault();
                if (editModalError) {
                    editModalError.textContent = 'No se puede dejar ningún campo vacío.';
                    editModalError.style.display = 'block';
                }
            }
        });
    }

    // --- Lógica de recorte de imagen ---
    (function initCropper() {
        const fileInput = document.getElementById('fileInput');
        const fileNameDisplay = document.getElementById('fileName');
        const cropModal = document.getElementById('cropModal');
        const cropCanvas = document.getElementById('cropCanvas');
        const imagenRecortadaInput = document.getElementById('imagenRecortada');
        const cancelCropBtn = document.getElementById('cancelCropBtn');
        const confirmCropBtn = document.getElementById('confirmCropBtn');

        if (!fileInput || !cropModal || !cropCanvas || !imagenRecortadaInput) {
            console.warn('[armario] Elementos de recorte no encontrados, se omitirá la funcionalidad de recorte');
            return;
        }

        const ctx = cropCanvas.getContext('2d');
        if (!ctx) {
            console.warn('[armario] No se ha podido obtener contexto 2D para el canvas de recorte');
            return;
        }

        let originalImage = null;
        let imgLoaded = false;
        let cropCenter = null;

        // Función para actualizar el nombre del archivo mostrado
        function actualizarNombreArchivo(nombre, tieneArchivo) {
            if (fileNameDisplay) {
                fileNameDisplay.textContent = nombre;
                if (tieneArchivo) {
                    fileNameDisplay.classList.add('has-file');
                } else {
                    fileNameDisplay.classList.remove('has-file');
                }
            }
        }

        function prepararCanvasConCuadroFijo(img) {
            const maxCanvasSize = 600;
            let iw = img.width;
            let ih = img.height;
            let scale = 1;

            if (iw > maxCanvasSize || ih > maxCanvasSize) {
                scale = Math.min(maxCanvasSize / iw, maxCanvasSize / ih);
                iw = iw * scale;
                ih = ih * scale;
            }

            cropCanvas.width = iw;
            cropCanvas.height = ih;

            ctx.clearRect(0, 0, iw, ih);
            ctx.drawImage(img, 0, 0, iw, ih);

            const scaleX = iw / img.width;
            const scaleY = ih / img.height;

            cropCenter = {
                x: img.width / 2,
                y: img.height / 2,
                side: 800
            };

            dibujarCuadroFijo();
        }

        function dibujarCuadroFijo() {
            if (!imgLoaded || !originalImage || !cropCenter) return;
            const iw = cropCanvas.width;
            const ih = cropCanvas.height;

            ctx.clearRect(0, 0, iw, ih);
            ctx.drawImage(originalImage, 0, 0, iw, ih);

            const scaleX = iw / originalImage.width;
            const scaleY = ih / originalImage.height;
            const scaleMin = Math.min(scaleX, scaleY);

            const sideCanvas = cropCenter.side * scaleMin;
            const halfSideCanvas = sideCanvas / 2;

            const centerCanvasX = cropCenter.x * scaleX;
            const centerCanvasY = cropCenter.y * scaleY;

            const x = centerCanvasX - halfSideCanvas;
            const y = centerCanvasY - halfSideCanvas;

            ctx.save();
            ctx.strokeStyle = 'red';
            ctx.lineWidth = 2;
            ctx.strokeRect(x, y, sideCanvas, sideCanvas);
            ctx.restore();
        }

        if (fileInput) {
            fileInput.addEventListener('change', function (e) {
                const file = e.target.files[0];
                if (!file) {
                    actualizarNombreArchivo('Ningún archivo seleccionado', false);
                    return;
                }

                // Resetear variables del cropper para nueva imagen
                originalImage = null;
                imgLoaded = false;
                cropCenter = null;

                // Actualizar nombre del archivo inmediatamente
                actualizarNombreArchivo(file.name, true);

                const reader = new FileReader();
                reader.onload = function (ev) {
                    originalImage = new Image();
                    originalImage.onload = function () {
                        imgLoaded = true;
                        const ow = originalImage.width;
                        const oh = originalImage.height;

                        if (ow <= 800 && oh <= 800) {
                            generarImagen800(originalImage, null, imagenRecortadaInput);
                            cropModal.style.display = 'none';
                        } else {
                            prepararCanvasConCuadroFijo(originalImage);
                            cropModal.style.display = 'flex';
                        }
                    };
                    originalImage.src = ev.target.result;
                };
                reader.readAsDataURL(file);
            });
        }

        cropCanvas.addEventListener('mousedown', function (e) {
            if (!imgLoaded || !cropCenter || !originalImage) return;
            const rect = cropCanvas.getBoundingClientRect();
            const cx = e.clientX - rect.left;
            const cy = e.clientY - rect.top;

            const iw = cropCanvas.width;
            const ih = cropCanvas.height;
            const scaleX = iw / originalImage.width;
            const scaleY = ih / originalImage.height;
            const scaleMin = Math.min(scaleX, scaleY);

            const halfSideCanvas = (cropCenter.side * scaleMin) / 2;
            const centerCanvasX = cropCenter.x * scaleX;
            const centerCanvasY = cropCenter.y * scaleY;

            const withinX = cx >= centerCanvasX - halfSideCanvas && cx <= centerCanvasX + halfSideCanvas;
            const withinY = cy >= centerCanvasY - halfSideCanvas && cy <= centerCanvasY + halfSideCanvas;

            if (withinX && withinY) {
                cropCanvas.isDragging = true;
                cropCanvas.dragOffsetX = cx - centerCanvasX;
                cropCanvas.dragOffsetY = cy - centerCanvasY;
            }
        });

        cropCanvas.addEventListener('mousemove', function (e) {
            if (!imgLoaded || !cropCenter || !cropCanvas.isDragging || !originalImage) return;
            const rect = cropCanvas.getBoundingClientRect();
            const cx = e.clientX - rect.left;
            const cy = e.clientY - rect.top;

            const iw = cropCanvas.width;
            const ih = cropCanvas.height;
            const scaleX = iw / originalImage.width;
            const scaleY = ih / originalImage.height;
            const scaleMin = Math.min(scaleX, scaleY);

            const halfSideCanvas = (cropCenter.side * scaleMin) / 2;

            let newCenterCanvasX = cx - cropCanvas.dragOffsetX;
            let newCenterCanvasY = cy - cropCanvas.dragOffsetY;

            newCenterCanvasX = Math.max(halfSideCanvas, Math.min(iw - halfSideCanvas, newCenterCanvasX));
            newCenterCanvasY = Math.max(halfSideCanvas, Math.min(ih - halfSideCanvas, newCenterCanvasY));

            cropCenter.x = newCenterCanvasX / scaleX;
            cropCenter.y = newCenterCanvasY / scaleY;

            dibujarCuadroFijo();
        });

        ['mouseup', 'mouseleave'].forEach(evt => {
            cropCanvas.addEventListener(evt, function () {
                cropCanvas.isDragging = false;
            });
        });

        if (cancelCropBtn) {
            cancelCropBtn.addEventListener('click', function () {
                imagenRecortadaInput.value = '';
                fileInput.value = '';
                cropModal.style.display = 'none';
                // Resetear el nombre de archivo mostrado
                const fileNameDisplay = document.getElementById('fileName');
                if (fileNameDisplay) {
                    fileNameDisplay.textContent = 'Ningún archivo seleccionado';
                    fileNameDisplay.classList.remove('has-file');
                }
            });
        }

        if (confirmCropBtn) {
            confirmCropBtn.addEventListener('click', function () {
                if (!imgLoaded || !originalImage || !cropCenter) {
                    cropModal.style.display = 'none';
                    return;
                }
                const recorte = {
                    x: cropCenter.x - cropCenter.side / 2,
                    y: cropCenter.y - cropCenter.side / 2,
                    w: cropCenter.side,
                    h: cropCenter.side
                };
                generarImagen800(originalImage, recorte, imagenRecortadaInput);
                cropModal.style.display = 'none';
            });
        }
    })();

    // Inicializar filtros si ya hay un tipo seleccionado
    const tipoActual = document.getElementById('filtroTipoSelect') ? document.getElementById('filtroTipoSelect').value : '';
    if (tipoActual) {
        mostrarCategoriasYTallasFiltroSinLimpiar();
    }

    // Envío del formulario de filtros
    const filtroForm = document.getElementById('filtroForm');
    if (filtroForm) {
        filtroForm.addEventListener('submit', function (e) {
            const categoriaUnificada = document.getElementById('categoriaUnificada');
            const catSup = document.getElementById('categoriaSuperiorSelect');
            const catInf = document.getElementById('categoriaInferiorSelect');
            const catCal = document.getElementById('categoriaCalzadoSelect');

            let valorCategoria = '';
            if (catSup && !catSup.closest('.hidden')) {
                valorCategoria = catSup.value;
            } else if (catInf && !catInf.closest('.hidden')) {
                valorCategoria = catInf.value;
            } else if (catCal && !catCal.closest('.hidden')) {
                valorCategoria = catCal.value;
            }

            if (categoriaUnificada) {
                categoriaUnificada.value = valorCategoria;
            }
        });
    }

    // --- Modal de eliminación de prenda ---
    let prendaIdToDelete = null;

    window.abrirModalEliminar = function(button) {
        if (!button) return;
        const prendaId = button.getAttribute('data-prenda-id');
        const prendaNombre = button.getAttribute('data-prenda-nombre');

        prendaIdToDelete = prendaId;
        const nombreEl = document.getElementById('deletePrendaNombre');
        if (nombreEl) {
            nombreEl.textContent = prendaNombre;
        }

        const infoConjuntos = document.getElementById('deleteConjuntosInfo');
        const infoNoConjuntos = document.getElementById('deleteNoConjuntosInfo');
        if (infoConjuntos) infoConjuntos.style.display = 'none';
        if (infoNoConjuntos) infoNoConjuntos.style.display = 'none';

        fetch('/armario/conjuntos-afectados/' + prendaId)
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    console.error('[armario] Error conjuntos afectados:', data.error);
                } else if (data.cantidad > 0) {
                    if (infoConjuntos) {
                        const cantidadEl = document.getElementById('deleteConjuntosCantidad');
                        const lista = document.getElementById('deleteConjuntosLista');
                        if (cantidadEl) cantidadEl.textContent = data.cantidad;
                        if (lista) {
                            lista.innerHTML = '';
                            data.nombres.forEach(nombre => {
                                const li = document.createElement('li');
                                li.textContent = nombre;
                                lista.appendChild(li);
                            });
                        }
                        infoConjuntos.style.display = 'block';
                    }
                } else if (infoNoConjuntos) {
                    infoNoConjuntos.style.display = 'block';
                }

                const overlay = document.getElementById('deleteModalOverlay');
                if (overlay) overlay.style.display = 'flex';
            })
            .catch(error => {
                console.error('[armario] Error al obtener conjuntos afectados:', error);
                if (infoNoConjuntos) infoNoConjuntos.style.display = 'block';
                const overlay = document.getElementById('deleteModalOverlay');
                if (overlay) overlay.style.display = 'flex';
            });
    };

    function cerrarModalEliminar() {
        const overlay = document.getElementById('deleteModalOverlay');
        if (overlay) overlay.style.display = 'none';
        prendaIdToDelete = null;
    }

    function confirmarEliminacion() {
        if (!prendaIdToDelete) return;
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/armario/eliminar';

        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'id';
        input.value = prendaIdToDelete;

        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }

    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    const deleteModalOverlay = document.getElementById('deleteModalOverlay');

    if (cancelDeleteBtn) {
        cancelDeleteBtn.addEventListener('click', cerrarModalEliminar);
    }
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', confirmarEliminacion);
    }
    if (deleteModalOverlay) {
        deleteModalOverlay.addEventListener('click', function (e) {
            if (e.target === deleteModalOverlay) {
                cerrarModalEliminar();
            }
        });
    }

    console.log('[armario] JS de armario inicializado correctamente');
});

