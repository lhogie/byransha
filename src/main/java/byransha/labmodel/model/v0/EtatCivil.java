package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.*;
import byransha.annotations.ListOptions;

public class EtatCivil extends BusinessNode {

    @Size(min = 2)
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

    public Out<DocumentNode> pic;

    public EtatCivil(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        name = new StringNode(g,  creator, InstantiationInfo.persisting);
        familyNameBeforeMariage = new  StringNode(g, creator, InstantiationInfo.persisting);
        firstName = new StringNode(g, creator, InstantiationInfo.persisting);
        birthDate = new DateNode(g, creator, InstantiationInfo.persisting, true);
        cityOfBirth = new StringNode(g, creator, InstantiationInfo.persisting);
        countryOfBirth = new ListNode<Country>(g, creator, InstantiationInfo.persisting);
        nationality = new ListNode<Nationality>(g, creator, InstantiationInfo.persisting);
        address = new StringNode(g, creator, InstantiationInfo.persisting);
        telephone = new PhoneNumberNode(g,  creator, InstantiationInfo.persisting);
        pic = new Out<>(g, creator, InstantiationInfo.persisting);
        this.setColor("#03fc62", creator);
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
