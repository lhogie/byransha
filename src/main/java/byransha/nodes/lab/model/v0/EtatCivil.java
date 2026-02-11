package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.annotations.*;
import byransha.annotations.ListOptions;
import byransha.nodes.DocumentNode;
import byransha.nodes.system.User;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.PhoneNumberNode;
import byransha.nodes.primitive.StringNode;

public class EtatCivil extends BusinessNode {

    @Size(min = 2)
    @Required
    public StringNode name, firstName;

    public StringNode familyNameBeforeMariage, cityOfBirth, address;

    @ListOptions(type = ListOptions.ListType.DROPDOWN, allowCreation = false)
    public ListNode<Country> countryOfBirth;

    @ListOptions(
            type = ListOptions.ListType.MULTIDROPDOWN,
            allowCreation = false,
            allowMultiple = true
    )    public ListNode<Nationality> nationality;

    public DateNode birthDate;

    @Pattern(
        regex = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$"
    )
    public PhoneNumberNode telephone;

    public DocumentNode pic;

    public EtatCivil(BBGraph g, User creator) {
        super(g, creator);
        name = new StringNode(g,  creator);
        familyNameBeforeMariage = new  StringNode(g, creator);
        firstName = new StringNode(g, creator);
        birthDate = new DateNode(g, creator);
        cityOfBirth = new StringNode(g, creator);
        countryOfBirth = new ListNode<Country>(g, creator);
        nationality = new ListNode<Nationality>(g, creator);
        address = new StringNode(g, creator);
        telephone = new PhoneNumberNode(g,  creator);
    }


    @Override
    public String whatIsThis() {
        return "Etat Civil";
    }

    @Override
    public String prettyName() {
        String prettyName = "";
        if(name != null && name.get() != null && !name.get().isBlank()) {
            prettyName = name.get();
        }
        if(firstName != null && firstName.get() != null && !firstName.get().isBlank()) {
            prettyName += " "+firstName.get();
        }
        if(prettyName.isBlank()) {
            prettyName = null;
        }
        return prettyName;
    }
}
