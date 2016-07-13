package hello;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {

    @Autowired
    CustomerRepository repository;

    /**
     * The currently edited customer
     */
    private Customer customer;

    /* Fields to edit properties in Customer entity */
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");

    /* Action buttons */
    private Button save = new Button("Save", FontAwesome.SAVE);
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", FontAwesome.TRASH_O);
    private CssLayout actions = new CssLayout(save, cancel, delete);

    public CustomerEditor() {
        addComponents(firstName, lastName, actions);

        // Configure and style components
        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        // wire action buttons to save, delete and reset
        save.addClickListener(event -> repository.save(customer));
        delete.addClickListener(event -> repository.delete(customer));
        cancel.addClickListener(event -> editCustomer(customer));
        setVisible(false);
    }

    interface ChangeHandler {
        void onChange();
    }

    final void editCustomer(Customer c) {
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            customer = repository.findOne(c.getId());
        } else {
            customer = c;
        }
        cancel.setVisible(persisted);

        /* Bind customer properties to similarly named fields
         Could also use annotation or "manual binding" or programmatically
         moving values from fields to entities before saving */
        BeanFieldGroup.bindFieldsUnbuffered(customer, this);

        setVisible(true);

        // A hack to ensure the whole form is visible
        save.focus();
        // Select all text in firstName field automatically
        firstName.selectAll();
    }

    void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete is clicked
        save.addClickListener(event -> h.onChange());
        delete.addClickListener(event -> h.onChange());
    }

}
