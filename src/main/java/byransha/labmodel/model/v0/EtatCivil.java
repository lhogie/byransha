package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.*;
import byransha.annotations.ListOptions;

public class EtatCivil extends BusinessNode {

    @Size(min = 2)
    public StringNode name;

    @Required
    public StringNode familyNameBeforeMariage, firstName, cityOfBirth, address;

    @ListOptions(type = ListOptions.ListType.DROPDOWN, allowCreation = false)
    public ListNode<Country> countryOfBirth;

    @ListOptions(type = ListOptions.ListType.DROPDOWN, allowCreation = false)
    public ListNode<Nationality> nationality;

    public DateNode birthDate;

    @ListOptions(
        type = ListOptions.ListType.MULTIDROPDOWN,
        allowCreation = false,
        allowMultiple = true
    )
    @Size(max = 2)
    public ListNode<Country> nationalites;

    @Pattern(
        regex = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$"
    )
    public PhoneNumberNode telephone;

    public ImageNode pic;

    public EtatCivil(BBGraph g) {
        super(g);
        name = new StringNode(g);
        familyNameBeforeMariage = new StringNode(g);
        firstName = new StringNode(g);
        birthDate = new DateNode(g);
        cityOfBirth = new StringNode(g);
        countryOfBirth = new ListNode(g);
        nationalites = new ListNode(g);
        address = new StringNode(g);
        telephone = new PhoneNumberNode(g);
        pic = new ImageNode(g);
        this.setColor("#03fc62");
    }

    public EtatCivil(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return "Etat Civil";
    }

    @Override
    public String prettyName() {
        if (name.get() != null && firstName.get() != null) {
            return name.get() + " " + firstName.get();
        } else if (name.get() != null && firstName.get() == null) {
            return name.get() + " (pas de prénom)";
        } else if (firstName.get() != null && name.get() == null) {
            return "(pas de nom) " + firstName.get();
        }
        return "Etat Civil sans information ";
    }
}
