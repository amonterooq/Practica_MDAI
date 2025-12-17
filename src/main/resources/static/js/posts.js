/**
 * Script para la página de posts.
 * Gestiona la funcionalidad de likes mediante AJAX.
 */

/**
 * Alterna el estado de like de un post.
 * Llama al endpoint API y actualiza la UI sin recargar la página.
 *
 * @param {number} postId - ID del post
 * @param {Event} event - Evento del click (para evitar propagación)
 */
function toggleLike(postId, event) {
    // Evitar que el evento se propague (importante para el doble click en la tarjeta)
    if (event) {
        event.stopPropagation();
    }

    console.log('Intentando dar like al post:', postId);

    // Llamar al endpoint API para alternar el like
    fetch('/posts/' + postId + '/toggle-like-api', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Error en la petición: ' + response.status);
    })
    .then(data => {
        console.log('Respuesta del servidor:', data);
        if (data.success) {
            // 1. Actualizar el contador
            const countSpan = document.getElementById('like-count-' + postId);
            if (countSpan) {
                countSpan.textContent = data.likesCount;
            }

            // 2. Actualizar el icono (color y relleno)
            const icon = document.getElementById('heart-icon-' + postId);
            if (icon) {
                if (data.liked) {
                    // Ahora es Like: Corazón sólido y rojo
                    icon.classList.remove('fa-regular');
                    icon.classList.add('fa-solid');
                    icon.style.color = '#e74c3c';
                } else {
                    // Ahora es Unlike: Corazón borde y gris
                    icon.classList.remove('fa-solid');
                    icon.classList.add('fa-regular');
                    icon.style.color = '#bdc3c7';
                }

                // Pequeña animación visual
                icon.style.transform = 'scale(1.3)';
                setTimeout(() => icon.style.transform = 'scale(1)', 200);
            }
        }
    })
    .catch(error => console.error('Error:', error));
}
