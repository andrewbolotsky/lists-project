package com.extremal.programming.controllers;


import java.util.List;

import com.extremal.programming.entity.ListEntity;
import com.extremal.programming.model.UserDto;
import com.extremal.programming.service.ListsService;
import com.extremal.programming.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class AuthenticationController {

    private UserService userService;
    private ListsService listsService;

    public AuthenticationController(UserService userService, ListsService listsService) {
        this.userService = userService;
        this.listsService = listsService;
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        log.debug("Opened login window");
        model.addAttribute("user", new UserDto());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") UserDto user, Model model) {
        log.info("{} {}", user.getUsername(), user.getPassword());

        if (false) {
            return "redirect:/login";
        }
        List<ListEntity> lists = listsService.getListEntitiesForConcreteUser(user.getUsername());
        model.addAttribute("items", lists);
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") UserDto user, Model model) {
        userService.createNewUser(user.getUsername(), user.getPassword());
        return "redirect:/home";
    }
}

