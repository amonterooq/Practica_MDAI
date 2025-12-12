document.addEventListener('DOMContentLoaded', function () {
    const bubble = document.getElementById('ai-chat-bubble');
    const windowEl = document.getElementById('ai-chat-window');
    const teaser = document.getElementById('ai-chat-teaser');
    const closeTeaserBtn = document.getElementById('ai-chat-teaser-close');
    const closeBtn = document.getElementById('ai-chat-close');
    const minimizeBtn = document.getElementById('ai-chat-minimize');
    const form = document.getElementById('ai-chat-form');
    const input = document.getElementById('ai-chat-input');
    const messagesContainer = document.querySelector('.ai-chat-messages');
    const sendButton = document.getElementById('ai-chat-send');

    if (!bubble || !windowEl || !form || !input || !messagesContainer) {
        return;
    }

    let isOpen = false;
    let isMinimized = false;

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

    const setSendingState = (sending) => {
        if (!sendButton) return;
        sendButton.disabled = sending;
    };

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

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const text = (input.value || '').trim();
        if (!text) {
            return;
        }

        appendMessage(text, 'user');
        input.value = '';

        const typingId = `typing-${Date.now()}`;
        const typingMsg = document.createElement('div');
        typingMsg.classList.add('ai-chat-message', 'ai-chat-message--bot', 'ai-chat-message--typing');
        typingMsg.id = typingId;
        typingMsg.textContent = 'El asistente está escribiendo...';
        messagesContainer.appendChild(typingMsg);
        scrollToBottom();

        setSendingState(true);

        fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                message: text
            })
        })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.json();
        })
        .then(function (data) {
            if (typingMsg.parentNode) {
                typingMsg.parentNode.removeChild(typingMsg);
            }
            const reply = data && data.reply ? data.reply : 'De momento soy un prototipo, pero pronto te podré recomendar conjuntos.';
            appendMessage(reply, 'bot');
        })
        .catch(function () {
            if (typingMsg.parentNode) {
                typingMsg.parentNode.removeChild(typingMsg);
            }
            appendMessage('Lo siento, ha ocurrido un error al hablar con el asistente. Inténtalo de nuevo más tarde.', 'bot');
        })
        .finally(function () {
            setSendingState(false);
        });
    });

    // Mostrar teaser al cargar (se puede ocultar con CSS por defecto y mostrar aquí si se desea)
    if (teaser) {
        teaser.classList.remove('ai-chat-teaser--hidden');
    }
});

