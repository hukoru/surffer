package com.martmoa.surffer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {


	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public void index(Model model) {
		model.addAttribute("example", "Hello World!");
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test(Model model) {
		model.addAttribute("example", "Hello World!");
	}


}
