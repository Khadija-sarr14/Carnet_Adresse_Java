@Controller
@RequestMapping("/contacts")
public class WebController {

    private final ContactService contactService;

    public WebController(ContactService contactService) {
        this.contactService = contactService;
    }

    // 📄 Afficher liste
    @GetMapping
    public String listContacts(Model model) {
        model.addAttribute("contacts", contactService.getAllContacts());
        return "contacts/list";
    }

    // ➕ Formulaire ajout
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "contacts/form";
    }

    // 💾 Enregistrer contact
    @PostMapping
    public String saveContact(@ModelAttribute Contact contact) {
        contactService.createContact(contact);
        return "redirect:/contacts";
    }
}