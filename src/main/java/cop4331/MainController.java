package cop4331;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping(path="/demo")
@RequestMapping(path="/")
public class MainController {
    @Autowired
    private UserRepository userRepository;

//    @PostMapping(path="/add")
    @PostMapping(path="/login")
    public @ResponseBody String addNewUser (@RequestParam String uName,
        @RequestParam String fName, @RequestParam String lName, @RequestParam String pWord) {

        User n = new User();
        n.setUName(uName);
        n.setFName(fName);
        n.setLName(lName);
        n.setPWord(pWord);
        userRepository.save(n);
        return "Saved\n";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
