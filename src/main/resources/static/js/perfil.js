// filepath: c:\Users\Fran\Desktop\Practica\src\main\resources\static\js\perfil.js

// Lógica del modal de eliminación de perfil (tomada del patrón de conjuntos)
const deletePerfilModalOverlay = document.getElementById('deletePerfilModalOverlay');
const cancelDeletePerfilBtn = document.getElementById('cancelDeletePerfilBtn');
const confirmDeletePerfilBtn = document.getElementById('confirmDeletePerfilBtn');

function abrirModalEliminarPerfil() {
    if (deletePerfilModalOverlay) {
        deletePerfilModalOverlay.style.display = 'flex';
    }
}

function cerrarModalEliminarPerfil() {
    if (deletePerfilModalOverlay) {
        deletePerfilModalOverlay.style.display = 'none';
    }
}

function confirmarEliminacionPerfil() {
    // Crear y enviar formulario POST a /usuarios/perfil/delete
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/usuarios/perfil/delete';
    document.body.appendChild(form);
    form.submit();
}

if (cancelDeletePerfilBtn) {
    cancelDeletePerfilBtn.addEventListener('click', cerrarModalEliminarPerfil);
}

if (confirmDeletePerfilBtn) {
    confirmDeletePerfilBtn.addEventListener('click', confirmarEliminacionPerfil);
}

if (deletePerfilModalOverlay) {
    deletePerfilModalOverlay.addEventListener('click', function(e) {
        if (e.target === this) {
            cerrarModalEliminarPerfil();
        }
    });
}

