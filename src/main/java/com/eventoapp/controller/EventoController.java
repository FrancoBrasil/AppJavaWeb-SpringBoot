package com.eventoapp.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.models.Convidado;
import com.eventoapp.models.Evento;
import com.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {
	
	@Autowired
	private EventoRepository repository;
	
	@Autowired
	private ConvidadoRepository convidadoRepository;
	
	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}
	
	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/cadastrarEvento";
		}
		repository.save(evento);
		attributes.addFlashAttribute("mensagem", "Evento cadastrado com sucesso");
		return "redirect:/cadastrarEvento";
		
	}
	
	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("index");
		Iterable<Evento> eventos = repository.findAll();
		mv.addObject("eventos", eventos);
		return mv;
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ModelAndView detalhesEvento(@PathVariable Long id) {
		Optional<Evento> evento = repository.findById(id);
		if (evento.isPresent()) {
			ModelAndView mv = new ModelAndView("evento/detalhesEvento");
			mv.addObject("eventos", evento.get());
			
			Iterable<Convidado> convidados = convidadoRepository.findByEvento(evento.get());
			mv.addObject("convidados", convidados);
			return mv;
		}
		throw new RuntimeException("Evento não encontrado!");
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.POST)
	public String detalhesEventoPost(@PathVariable Long id, @Valid Convidado convidado, 
			BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos");
			return "redirect:/{id}";
		}
		Optional<Evento> evento = repository.findById(id);
		if (evento.isPresent()) {
			convidado.setEvento(evento.get());
			convidadoRepository.save(convidado);
			attributes.addFlashAttribute("mensagem", "Convidado salvo com sucesso");
			return "redirect:/{id}";
		}
		
		return null;
	}
}
