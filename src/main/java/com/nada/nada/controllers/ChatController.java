package com.nada.nada.controllers;

import com.nada.nada.dto.chat.ChatRequestDto;
import com.nada.nada.dto.chat.ChatResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequestDto request) {
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ChatResponseDto("Necesito que escribas algún mensaje para poder ayudarte."));
        }

        String message = request.getMessage().toLowerCase();
        String reply;

        if (message.contains("frío") || message.contains("invierno")) {
            reply = "Parece que hace frío. Te recomendaría combinar capas: una prenda superior abrigada, pantalones largos y un calzado cerrado.";
        } else if (message.contains("calor") || message.contains("verano")) {
            reply = "Para días de calor, busca prendas ligeras y transpirables, colores claros y calzado cómodo, como zapatillas o sandalias cerradas.";
        } else if (message.contains("cita") || message.contains("noche")) {
            reply = "Para una cita de noche, puedes elegir una parte de arriba más elegante, una parte de abajo neutra y unos zapatos que destaquen sin perder comodidad.";
        } else {
            reply = "De momento soy un prototipo, pero pronto te podré recomendar conjuntos específicos según tu armario y la ocasión.";
        }

        return ResponseEntity.ok(new ChatResponseDto(reply));
    }
}
