package com.finance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class SwaggerController {

	// ========== REDIRECT TO SWAGGER ==========
	@GetMapping("/")
	public void redirectToSwagger(HttpServletResponse response) throws IOException {
	    response.sendRedirect("/swagger-ui.html");
	}
}