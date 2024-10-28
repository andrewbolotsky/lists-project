package com.extremal.programming.controllers;

import com.extremal.programming.model.List;
import com.extremal.programming.service.ListsService;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Data
@Controller
public class ListsController {

    private ListsService listsService;

    public ListsController(ListsService listsService) {
        this.listsService = listsService;
    }

    @GetMapping("/list_page")
    public String getList(Model model) {
        model.addAttribute("itemListForm", new List());
        return "redirect:/add";
    }

    @PostMapping("/addList")
    public String addList(@ModelAttribute("itemListForm") List list, Model model) {
        Long id = listsService.createNewList(list.name).getId();
        String[] nodes = list.nodes.split(",");
        for (String node : nodes) {
            listsService.addNodeToList(id, node);
        }
        return "redirect:/home";
    }

}
