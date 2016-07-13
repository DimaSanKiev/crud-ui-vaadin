package hello;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    private final CustomerRepository repository;
    private final CustomerEditor editor;
    private final Grid grid;
    private final TextField filter;
    private final Button addNewBtn;

    @Autowired
    public VaadinUI(CustomerRepository repository, CustomerEditor editor) {
        this.repository = repository;
        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New Customer", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {
        // Build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);

        // Configure layouts and components
        actions.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        grid.setHeight(300, Unit.PIXELS);
        grid.setColumns("id", "firstName", "lastName");

        filter.setInputPrompt("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.addTextChangeListener(event -> listCustomers(event.getText()));

        // Connect selected Customer to editor or hide if none is selected
        grid.addSelectionListener(event -> {
            if (event.getSelected().isEmpty()) {
                editor.setVisible(false);
            } else {
                editor.editCustomer((Customer) grid.getSelectedRow());
            }
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(event -> editor.editCustomer(new Customer("", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
    }

    // tag::listCustomers[]
    private void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setContainerDataSource(
                    new BeanItemContainer<>(Customer.class, repository.findAll()));
        } else {
            grid.setContainerDataSource(
                    new BeanItemContainer<>(Customer.class, repository.findByLastNameStartsWithIgnoreCase(text))
            );
        }
    }
    // end::listCustomers[]

}
