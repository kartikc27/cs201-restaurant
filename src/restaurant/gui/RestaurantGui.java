package restaurant.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	
	public AnimationPanel animationPanel;
	//JPanel InfoLayout;
	

    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel;
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JPanel myPanel;
    private JLabel infoLabel; //part of infoPanel
    private JLabel myLabel;
    private JCheckBox stateCB;//part of infoLabel
    private JPanel InfoLayout; 

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animationPanel = new AnimationPanel();
        animationPanel.setVisible(true);
    	
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	setBounds(0,0,screenSize.width, screenSize.height);
       

        Dimension restDim = new Dimension(screenSize.width, (int) (screenSize.height * .75));
        restPanel = new RestaurantPanel(this);
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        
        InfoLayout = new JPanel();

        InfoLayout.setLayout(new BoxLayout(InfoLayout, BoxLayout.Y_AXIS));
        InfoLayout.add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(screenSize.width, (int) (screenSize.height * .22));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        
        
        InfoLayout.add(infoPanel);
        
        setLayout(new GridLayout(0,2));
        
        
        //Dimension dim = new Dimension (screenSize.width/2, screenSize.height);
        //InfoLayout.setPreferredSize(dim);
        add (InfoLayout);
        //animationPanel.setPreferredSize(dim);
        add (animationPanel);
       // System.out.println (animationPanel.getPreferredSize());
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
            infoPanel.validate();
        }
        
        if (person instanceof WaiterAgent) {
            WaiterAgent waiter = (WaiterAgent) person;
            stateCB.setText("Break?");
          //Should checkmark be there? 
            stateCB.setSelected(waiter.getGui().IsOnBreak());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!waiter.getGui().IsOnBreak());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
            infoPanel.validate();
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof WaiterAgent) {
                WaiterAgent w = (WaiterAgent) currentPerson;
                w.getGui().IsOnBreak();
                stateCB.setEnabled(false);
            }
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    
    public void setWaiterEnabled(WaiterAgent w) {
        if (currentPerson instanceof WaiterAgent) {
            WaiterAgent waiter = (WaiterAgent) currentPerson;
            if (w.equals(waiter)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("Rami's Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
