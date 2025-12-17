// filepath: c:\Users\Fran\Desktop\Practica\src\main\resources\static\js\conjuntos.js

// --- Lógica del modal de edición ---
const editModalOverlay = document.getElementById('editModalOverlay');
const closeEditModalBtn = document.getElementById('closeEditModalBtn');

function abrirModalEdicion(card) {
    const id = card.getAttribute('data-conjunto-id');
    const nombre = card.getAttribute('data-conjunto-nombre');
    const descripcion = card.getAttribute('data-conjunto-descripcion');
    const superiorId = card.getAttribute('data-superior-id');
    const inferiorId = card.getAttribute('data-inferior-id');
    const calzadoId = card.getAttribute('data-calzado-id');

    document.getElementById('editId').value = id;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editDescripcion').value = descripcion || '';
    document.getElementById('editPrendaSuperiorId').value = superiorId;
    document.getElementById('editPrendaInferiorId').value = inferiorId;
    document.getElementById('editPrendaCalzadoId').value = calzadoId;

    editModalOverlay.style.display = 'flex';
}

function cerrarModalEdicion() {
    editModalOverlay.style.display = 'none';
}

if (closeEditModalBtn) {
    closeEditModalBtn.addEventListener('click', cerrarModalEdicion);
}

if (editModalOverlay) {
    editModalOverlay.addEventListener('click', function(e) {
        if (e.target === editModalOverlay) {
            cerrarModalEdicion();
        }
    });
}

// --- Lógica del modal de eliminación ---
let conjuntoIdToDelete = null;

function abrirModalEliminar(button) {
    const conjuntoId = button.getAttribute('data-conjunto-id');
    const conjuntoNombre = button.getAttribute('data-conjunto-nombre');

    conjuntoIdToDelete = conjuntoId;
    document.getElementById('deleteConjuntoNombre').textContent = conjuntoNombre;
    document.getElementById('deleteModalOverlay').style.display = 'flex';
}

function cerrarModalEliminar() {
    document.getElementById('deleteModalOverlay').style.display = 'none';
    conjuntoIdToDelete = null;
}

function confirmarEliminacion() {
    if (!conjuntoIdToDelete) return;

    // Crear un formulario y enviarlo
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/conjuntos/eliminar';

    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'id';
    input.value = conjuntoIdToDelete;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}

// Event listeners para el modal de eliminación
document.addEventListener('DOMContentLoaded', function() {
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
        deleteModalOverlay.addEventListener('click', function(e) {
            if (e.target === this) {
                cerrarModalEliminar();
            }
        });
    }

    // --- Preview de imagen en selects ---
    initPreviewSelects();
});

/**
 * Inicializa la funcionalidad de preview de imagen para los selects de prendas.
 * Muestra una miniatura de la prenda seleccionada debajo del select.
 */
function initPreviewSelects() {
    const selects = [
        { selectId: 'selectSuperior', previewId: 'previewSuperior' },
        { selectId: 'selectInferior', previewId: 'previewInferior' },
        { selectId: 'selectCalzado', previewId: 'previewCalzado' }
    ];

    selects.forEach(({ selectId, previewId }) => {
        const select = document.getElementById(selectId);
        const previewContainer = document.getElementById(previewId);

        if (select && previewContainer) {
            // Mostrar preview cuando cambia la selección
            select.addEventListener('change', function() {
                actualizarPreview(this, previewContainer);
            });

            // Si ya hay una opción seleccionada al cargar, mostrar preview
            if (select.value) {
                actualizarPreview(select, previewContainer);
            }
        }
    });
}

/**
 * Actualiza el contenedor de preview con la imagen de la opción seleccionada.
 */
function actualizarPreview(select, previewContainer) {
    const selectedOption = select.options[select.selectedIndex];
    const imagenUrl = selectedOption ? selectedOption.getAttribute('data-imagen') : null;

    if (imagenUrl && imagenUrl !== 'null' && imagenUrl.trim() !== '') {
        previewContainer.innerHTML = `
            <div class="preview-label">Vista previa</div>
            <img src="${imagenUrl}" alt="Preview de prenda" onerror="this.parentElement.classList.remove('active'); this.parentElement.innerHTML='';">
        `;
        previewContainer.classList.add('active');
    } else {
        previewContainer.innerHTML = '';
        previewContainer.classList.remove('active');
    }
}
