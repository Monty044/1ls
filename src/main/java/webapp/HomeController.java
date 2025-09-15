package webapp;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class HomeController {
    private final PersonRepository repo;
    public HomeController(PersonRepository repo) { this.repo = repo; }

    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("form", new Person());
        model.addAttribute("countries", countries());
        return "index";
    }

    @PostMapping("/submit")
    String submit(@ModelAttribute("form") @Valid Person form, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("countries", countries());
            return "index";
        }
        repo.save(new Person(form.getName().trim(), form.getCountry()));
        return "redirect:/stats";
    }

    @GetMapping("/stats")
    String stats(Model model) {
        model.addAttribute("rows", repo.aggregate());
        return "stats";
    }

    private List<String> countries() {
        var names = new ArrayList<String>();
        for (var iso : Locale.getISOCountries()) {
            var name = new Locale("sv", iso).getDisplayCountry(new Locale("sv"));
            if (!name.isBlank()) names.add(name);
        }
        names.sort(Comparator.naturalOrder());
        return names;
    }
}
