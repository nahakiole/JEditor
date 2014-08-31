import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;


public class Editor extends JFrame {

    private JTextArea editField = new JTextArea();
    private File currentFile = null;
    private Integer lastFound = null;
    private String searchString = null;
    private Highlighter editFieldHighlighter = editField.getHighlighter();

    public Editor() {
        createMenuBar();
        createEditField();
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        URL iconURL = getClass().getResource("/icon.png");
// iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());
    }

    public void createMenuBar() {
        JMenuBar menuBar;
        JMenu fileMenu;
        JMenu modifyMenu;
        menuBar = new JMenuBar();
        fileMenu = new JMenu("Datei");
        modifyMenu = new JMenu("Bearbeiten");

        Integer shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        modifyMenu.add(createJMenuItem("Suchen", new SearchActionListener(), KeyEvent.VK_F,shortcutKey));
        modifyMenu.add(createJMenuItem("Next", new NextSearchActionListener(), KeyEvent.VK_H, InputEvent.CTRL_MASK));
        modifyMenu.add(createJMenuItem("Previous", new PrevSearchActionListener(), KeyEvent.VK_L,InputEvent.CTRL_MASK));


        fileMenu.add(createJMenuItem("Neu", new NewActionListener(), KeyEvent.VK_N,shortcutKey));
        fileMenu.add(createJMenuItem("Open", new OpenActionListener(), KeyEvent.VK_O,shortcutKey));
        fileMenu.add(createJMenuItem("Speichern", new SaveActionListener(), KeyEvent.VK_S,shortcutKey));
        fileMenu.add(createJMenuItem("Speichern unter..", new SaveAsActionListener(), null,shortcutKey));
        fileMenu.addSeparator();
        fileMenu.add(createJMenuItem("Beenden", new CloseActionListener(), KeyEvent.VK_Q,shortcutKey));
        menuBar.add(fileMenu);
        menuBar.add(modifyMenu);
        setJMenuBar(menuBar);
    }

    public JMenuItem createJMenuItem(String name, ActionListener actionListener, Integer keyevent, Integer modifier) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(actionListener);
        if (keyevent != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
                    keyevent, modifier));
        }

        return menuItem;
    }

    public void createEditField() {
        JPanel editPanel = new JPanel();
        editField.setEditable(true);
        editPanel.add(editField);
        editPanel.setLayout(new GridLayout(1, 1));
        getContentPane().add(editPanel);
    }

    public static void main(String[] args) {
        Editor editor = new Editor();
        editor.setLocation(100, 100);
        editor.setVisible(true);
    }

    public void setTitle(String name) {
        if (name.equals("")) {

            super.setTitle("Awesome Editor");
        } else {

            super.setTitle("Awesome Editor: Editing " + name);
        }
    }

    class OpenActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Only Text and Java Files", "txt", "java");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(Editor.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                FileReader reader = null;
                try {
                    reader = new FileReader(file);
                    BufferedReader br = new BufferedReader(reader);
                    editField.read(br, null);
                    editField.requestFocus();
                    br.close();
                    currentFile = file;
                    setTitle(file.getAbsolutePath());

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getAbsolutePath());
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
    }

    class NewActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            setTitle("");
            currentFile = null;
            editField.setText("");
        }
    }

    class SaveActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentFile != null) {
                FileWriter writer = null;
                try {
                    writer = new FileWriter(currentFile);
                    BufferedWriter bw = new BufferedWriter(writer);
                    editField.write(bw);
                    bw.close();
                    editField.requestFocus();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        }
    }

    class SaveAsActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(Editor.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileWriter writer = null;
                try {
                    writer = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(writer);
                    editField.write(bw);
                    bw.close();
                    editField.requestFocus();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    class CloseActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            lastFound = 0;
            searchString = (String) JOptionPane.showInputDialog(
                    Editor.this,
                    "",
                    "Suchen nach ", JOptionPane.INFORMATION_MESSAGE,
                    null, null, "");
            editFieldHighlighter.removeAllHighlights();
            int pos = editField.getText().indexOf(searchString, 0);
            lastFound = pos;
            try {
                editFieldHighlighter.addHighlight(pos,
                        pos + searchString.length(),
                        DefaultHighlighter.DefaultPainter);
            } catch (BadLocationException e1) {
                JOptionPane.showMessageDialog(Editor.this, "String "+searchString+" could not be found.");
            }
        }
    }

    private class NextSearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int pos = editField.getText().indexOf(searchString, lastFound+1);
            lastFound = pos;

            editFieldHighlighter.removeAllHighlights();
            try {
                editFieldHighlighter.addHighlight(pos,
                        pos + searchString.length(),
                        DefaultHighlighter.DefaultPainter);
            } catch (BadLocationException e1) {
                JOptionPane.showMessageDialog(Editor.this, "No more occurrences of "+searchString+" could not be found.");
            }
        }
    }

    private class PrevSearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int pos = editField.getText().lastIndexOf(searchString, lastFound+1);
            lastFound = pos;

            editFieldHighlighter.removeAllHighlights();
            try {
                editFieldHighlighter.addHighlight(pos,
                        pos + searchString.length(),
                        DefaultHighlighter.DefaultPainter);
            } catch (BadLocationException e1) {
                JOptionPane.showMessageDialog(Editor.this, "No more occurrences of "+searchString+" could not be found.");
            }
        }
    }
}
