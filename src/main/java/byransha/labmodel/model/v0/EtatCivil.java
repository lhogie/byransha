package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.*;
import byransha.annotations.ListOptions;

public class EtatCivil extends BusinessNode {

    @Size(min = 2)
    public Out<StringNode> name;

    @Required
    public Out<StringNode> familyNameBeforeMariage, firstName, cityOfBirth, address;

    @ListOptions(type = ListOptions.ListType.DROPDOWN, allowCreation = false)
    public Out<ListNode<Country>> countryOfBirth;

    @ListOptions(type = ListOptions.ListType.DROPDOWN, allowCreation = false)
    public Out<ListNode<Nationality>> nationality;

    public Out<DateNode> birthDate;

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

    public EtatCivil(BBGraph g, User creator) {
        super(g, creator);
        name = new Out<StringNode>(g,  creator);
        familyNameBeforeMariage = new Out<StringNode>(g, creator);
        firstName = new Out<StringNode>(g, creator);
        birthDate = new Out< DateNode>(g, creator);
        cityOfBirth = new Out< StringNode>(g, creator);
        countryOfBirth = new Out< ListNode<Country>>(g, creator);
        nationalites = new Out< ListNode<Country>>(g, creator);
        address = new Out< StringNode>(g, creator);
        telephone = new Out< PhoneNumberNode>(g,  creator);
        pic = new Out< ImageNode>(g, creator);
        this.setColor("#03fc62", creator);
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
