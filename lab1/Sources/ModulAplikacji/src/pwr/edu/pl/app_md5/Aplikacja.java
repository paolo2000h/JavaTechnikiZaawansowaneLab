package pwr.edu.pl.app_md5;

import pwr.edu.pl.md5.CheckFile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Aplikacja extends JFrame{
    private JButton saveSnapshotButton;
    private JPanel mainPanel;
    private JTable table1;
    private JButton checkFilesButton;
    private JScrollPane panel2;
    private DefaultTableModel defaultTableModel;
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Aplikacja frame = new Aplikacja();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Aplikacja() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("LAB1 - Zmiany w plikach");
        setBounds(100, 100, 500, 500);
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainPanel);


        saveSnapshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fc.showOpenDialog(saveSnapshotButton);

                if(result == JFileChooser.APPROVE_OPTION)
                {
                    String directoryName = fc.getSelectedFile().getName();
                    try {
                        HashMap<String, String> snapshotHashMap = new HashMap<>();
                        snapshotHashMap = CheckFile.generateMd5HashMap(fc.getSelectedFile().toString());
                        CheckFile.writeToFile(snapshotHashMap, directoryName);
                        JOptionPane.showMessageDialog(saveSnapshotButton, "Utworzono snapshot");
                    } catch (NoSuchAlgorithmException e1) {
                        JOptionPane.showMessageDialog(saveSnapshotButton, "Error");
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(saveSnapshotButton, "Error");
                        e1.printStackTrace();
                    }
                }

                else {
                    JOptionPane.showMessageDialog(saveSnapshotButton, "Brak wybranego folderu");
                }
            }
        });


        checkFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<String, String> snapshotHashMap = new HashMap<String, String>();
                HashMap<String, String> currentHashMap = new HashMap<String, String>();
                HashMap<String, Boolean> resultHashMap = new HashMap<String, Boolean>();
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fc.showOpenDialog(saveSnapshotButton);

                if(result == JFileChooser.APPROVE_OPTION) {
                    String directoryName = fc.getSelectedFile().getName();
                    try {
                        currentHashMap = CheckFile.generateMd5HashMap(fc.getSelectedFile().toString());

                        try {
                            snapshotHashMap = CheckFile.readFromFile(directoryName);
                        } catch (FileNotFoundException e2) {
                            JOptionPane.showMessageDialog(checkFilesButton, "Brak wykonanego snaphota");
                        }

                        resultHashMap = CheckFile.compare(snapshotHashMap, currentHashMap);

                        createUIComponents();
                        table1.getTableHeader().setResizingAllowed(false);
                        table1.setEnabled(false);
                        System.out.println(resultHashMap.entrySet());

                        for (Map.Entry<String, Boolean> entry : resultHashMap.entrySet()) {
                            String fileName = entry.getKey();
                            Boolean fileStatus = entry.getValue();
                            Vector<String> vector = new Vector<>();
                            vector.add(fileName);
                            if(fileStatus)
                                vector.add("MODIFIED");
                            else
                                vector.add("unmodified");

                            defaultTableModel.addRow(vector);

                            panel2.setViewportView(table1);


                        }


                    } catch (NoSuchAlgorithmException | IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(checkFilesButton, "Error");
                    }

                }
                else
                    JOptionPane.showMessageDialog(saveSnapshotButton, "Nie wybrano folderu");
            }
        });
    }

    public void createUIComponents() {
        table1 = new JTable();
        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Nazwa pliku");
        defaultTableModel.addColumn("Status");
        table1.setModel(defaultTableModel);
    }
}
