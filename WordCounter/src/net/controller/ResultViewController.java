package net.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/*
 * author: Crunchify.com
 * 
 */

@Controller
public class ResultViewController {

	@RequestMapping("/words")
	public ModelAndView displayCountedWords() {

		String message = "<br><div style='text-align:center;'>" + "<h3>To be implemented!</div><br><br>";
		return new ModelAndView("words", "message", message);
	}
}