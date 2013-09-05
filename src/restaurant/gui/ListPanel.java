package restaurant.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import restaurant.CustomerAgent;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener, KeyListener{

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    private JTextField nameField = new JTextField(10);

    private RestaurantPanel restPanel;
    private String type;
    private String name;
    
    public JPanel namePane = new JPanel();
    public JCheckBox isHungry = new JCheckBox();
    

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);

        nameField.setHorizontalAlignment(JTextField.CENTER);
        namePane.setLayout(new FlowLayout());

        isHungry.addActionListener(this);
        isHungry.setVisible(true);
        isHungry.setText("Hungry?");

        
        namePane.add(addPersonB);
        namePane.add(nameField);
        namePane.add(isHungry);
        
        nameField.addKeyListener(this);

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        
        pane.setMinimumSize(new Dimension(100, 100));
        pane.setPreferredSize(new Dimension(150, 150));  
        
        add(namePane);
        add(pane);
        
        isHungry.setEnabled(false);
        addPersonB.setEnabled(false);
    }
    
    
    public void keyTyped(KeyEvent e) {
    	name = nameField.getText();
    	if ((name != null) && !name.isEmpty()){
    		isHungry.setEnabled(true);
            addPersonB.setEnabled(true);
    	}
    }
    		
    	
    

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	name = nameField.getText();
        	nameField.setText(null);
        	
        	if (name != null && !name.isEmpty()){
        		addPerson(name);
        		isHungry.setEnabled(false);
                addPersonB.setEnabled(false);
        		if (isHungry.isSelected())
        		{
        			isHungry.setSelected(false);
        			restPanel.markHungry(name);
        		}
        		
        	}
        	
        }
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height/7));
       
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);

            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson(type, name);//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }


	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
