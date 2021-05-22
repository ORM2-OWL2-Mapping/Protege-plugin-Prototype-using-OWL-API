import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class GUIForm extends JFrame{
    private JPanel rootPanel;
    private JComboBox typeTranslateComboBox;
    private JList testList;
    private JScrollPane scrollPane1;
    private JLabel imageLabel;
    private JButton runSelectTestBtn;
    private JButton runAllTestsBtn;

    public GUIForm() {
        setTitle("ORM-OWL Tester");
        add(rootPanel);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ArrayList<String> translateTypes = new ArrayList<String>();
        translateTypes.add("ORM-OWL");
        translateTypes.add("OWL-ORM");
        fillComboBox(typeTranslateComboBox, translateTypes);
        ArrayList<String> testListValues = new ArrayList<String>();
        testListValues.add("Тест 1");
        testListValues.add("Тест 2");
        fillList(testList, testListValues);

        typeTranslateComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = typeTranslateComboBox.getSelectedItem();
//                if (item != null) {
//                    String value = ((ComboItem)item).getValue();
//                    fillList(list1, Main.getShowplaceListByClass(value));
//                }
            }
        });
        testList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Object item = testList.getSelectedValue();
                if (item != null) {
                    String value = ((ComboItem)item).getValue();
                    try {
                        Image img = ImageIO.read(new File("ORMModel Small.jpg"));
                        img = img.getScaledInstance(img.getWidth(null), img.getHeight(null), Image.SCALE_DEFAULT);
                        imageLabel.setIcon(new ImageIcon(img));

                        //File file = new File("ORMModel Small.jpg");
                        //String localUrl = file.toURI().toURL().toString();
                        //Image img = new Image();

//                        BufferedImage src = ImageIO.read(new File("ORMModel Small.jpg"));
//                        final int width = src.getWidth(null);
//                        final int height = src.getHeight(null);
//                        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//                        Graphics graphics = image.getGraphics();
//                        graphics.drawImage(src, 0, 0, 200, 100, null);
//                        graphics.dispose();
//                        imageLabel.setIcon(new ImageIcon(image));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                }
            }
        });
        runSelectTestBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
//                String query = textArea1.getText();
//                if (query.equals("")) {
//                    JOptionPane.showMessageDialog(rootPanel, "Напишите запрос");
//                } else {
//                    fillTableSPARQL(Main.runQuery(query));
//                    //textPane1.setText(Main.runQuery(query));
//                }
            }
        });
    }

    public void fillComboBox(JComboBox comboBox, ArrayList<String> values) {
        for (String item : values) {
            comboBox.addItem(new ComboItem(item));
        }
        comboBox.setSelectedIndex(-1);
    }

    public void fillList(JList list, ArrayList<String> values) {
        DefaultListModel listModel = new DefaultListModel();
        for (String item : values) {
            listModel.addElement(new ComboItem(item));
        }
        list.setModel(listModel);
    }

    public void enableVisible() {
        setVisible(true);
    }

}

class ComboItem {
    private String value;

    public ComboItem(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return this.value;
    }
}
