package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.*;
import byransha.annotations.ListOptions;

public class EtatCivil extends BusinessNode {


    @Required
    public StringNode name, familyNameBeforeMariage, firstName, cityOfBirth, address;

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
    public Out<ListNode<Country>> nationalites;

    @Pattern(
        regex = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$"
    )
    public Out<PhoneNumberNode> telephone;

    public Out<DocumentNode> pic;

    public EtatCivil(BBGraph g, User creator) {
        super(g, creator);
        name = new StringNode(g,  creator);
        familyNameBeforeMariage = new StringNode(g, creator);
        firstName = new StringNode(g, creator);
        birthDate = new Out< DateNode>(g, creator);
        cityOfBirth = new StringNode(g, creator);
        countryOfBirth = new Out< ListNode<Country>>(g, creator);
        nationalites = new Out< ListNode<Country>>(g, creator);
        address = new StringNode(g, creator);
        telephone = new Out< PhoneNumberNode>(g,  creator);
        pic = new Out< DocumentNode>(g, creator);
        this.setColor("#03fc62", creator);
        endOfConstructor();
    }

    public EtatCivil(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    @Override
    public String whatIsThis() {
        return "Etat Civil";
    }

    @Override
    public String prettyName() {
        return  null;
    }
}
