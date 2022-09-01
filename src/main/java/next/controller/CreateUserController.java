package next.controller;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.db.DataBase;
import core.mvc.ModelAndView;
import next.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CreateUserController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public ModelAndView create(String userId, String password, String name, String email) {
        User user = new User(userId, password, name, email);
        log.debug("User : {}", user);

        DataBase.addUser(user);
        return ModelAndView.fromJspView("redirect:/");
    }
}
