import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.SwingConstants;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AppView {

    private JFrame frame;
    private JLabel labelForImage;
    private TextArea textArea;
    private JTextField selectedDirectoryTextField;
    private File filePath;
    private JPanel panel;
    private JPanel contentPane;
    private JLabel fileLoadedFromInfoLabel;
    private ImageIcon image;
    private ArrayList<String> readList;
    private WeakHashMap<String,FileContainer> weakHashMap;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AppView window = new AppView();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public AppView() throws IOException {
        weakHashMap = new WeakHashMap<>();
        initialize();
    }

    private void initialize() throws IOException {
        frame = new JFrame();
        frame.setBounds(100, 100, 1042, 641);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Current catalog:");
        DefaultListModel<String> defaultListModel = new DefaultListModel<String>();
        JScrollPane scrollPane = new JScrollPane();
        JList<String> filesList = new JList<String>(defaultListModel);
        JButton selectDirectoryButton = new JButton("Choose catalog");
        Font font = new Font("Serif", 0, 20);

        fileLoadedFromInfoLabel = new JLabel("");
        selectedDirectoryTextField = new JTextField();
        panel = new JPanel();
        textArea = new TextArea();
        labelForImage = new JLabel();

        lblNewLabel.setBounds(23, 32, 180, 26);
        panel.setBounds(23, 69, 493, 450);
        textArea.setBounds(532, 35, 467, 484);
        labelForImage.setBounds(850, 50, 100, 100);
        scrollPane.setBounds(10, 11, 473, 434);
        selectDirectoryButton.setBounds(189, 526, 171, 40);
        textArea.setBounds(532, 35, 467, 484);
        selectedDirectoryTextField.setBounds(189, 32, 311, 26);
        fileLoadedFromInfoLabel.setBounds(532, 523, 467, 40);

        panel.setLayout(null);
        panel.add(scrollPane);
        scrollPane.setViewportView(filesList);
        selectedDirectoryTextField.setText("blank");
        selectedDirectoryTextField.setEditable(false);
        textArea.setBackground(Color.white);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setFont(font);
        fileLoadedFromInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fileLoadedFromInfoLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        fileLoadedFromInfoLabel.setForeground(Color.RED);
        labelForImage.setVisible(false);


        selectDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


               // textArea.setVisible(false);
                labelForImage.setVisible(false);
                fileLoadedFromInfoLabel.setText("");

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if(fileChooser.showOpenDialog(null)!=0) {
                    JOptionPane.showMessageDialog(null, "No catalog");
                }
                else {
                    filePath = fileChooser.getSelectedFile();
                    String directoryPath = fileChooser.getSelectedFile().toString();

                    Set<String> directorySet = createSetOfDirectory(directoryPath);

                    defaultListModel.removeAllElements();

                    for (String directoryName : directorySet)
                        defaultListModel.addElement(directoryName);

                    selectedDirectoryTextField.setText(directoryPath);
                }

            }
        });



        filesList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting() && filesList.getSelectedValue() != null) {

                    image = loadPNG(filesList.getSelectedValue());
                    readList = loadRecord(filesList.getSelectedValue());
                    labelForImage.setVisible(true);
                    labelForImage.setIcon(image);
                    textArea.setVisible(true);
                    textArea.setText("");
                    for (String record : readList) {
                        textArea.append(record + "\n\r");
                    }
                }
            }
        });

        frame.getContentPane().add(selectDirectoryButton);
        frame.getContentPane().add(lblNewLabel);
        frame.getContentPane().add(labelForImage);
        frame.getContentPane().add(panel);
        frame.getContentPane().add(textArea);
        frame.getContentPane().add(selectedDirectoryTextField);
        frame.getContentPane().add(textArea);
        frame.getContentPane().add(fileLoadedFromInfoLabel);
    }

    /*
    Funkcja zwracajaca nazwy katalogow
     */
    public static Set<String> createSetOfDirectory(String dir) {
        Set<String> directoryNameSet;
        try {
            directoryNameSet = Files.list(Paths.get(dir)).filter(Files::isDirectory).map(Path::getFileName)
                    .map(Path::toString).collect(Collectors.toSet());

            return directoryNameSet;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Err");
        }

        return null;
    }

    /*
     Pobranie obrazka z pamieci lub z wykorzystaniem Weak References
     */
    public ImageIcon loadPNG(String directoryName) {

        File file;
        FileContainer fileContainer;
        ImageIcon imageIcon;

        try {
            if (!weakHashMap.containsKey(directoryName+"\\"+"image.png")) {
                file = new File(filePath.getAbsolutePath() + "\\" + directoryName+"\\"+"image.png");
                fileContainer = new FileContainer();
                imageIcon = createIcon(file);
                fileContainer.setIcon(imageIcon);
                weakHashMap.put(directoryName+"\\"+"image.png", fileContainer);
                fileLoadedFromInfoLabel.setText("PNG Loaded from file");
                return imageIcon;
            } else {
                fileLoadedFromInfoLabel.setText("PNG Loaded using Weak Reference");
                imageIcon= weakHashMap.get(directoryName+"\\"+"image.png").getIcon();
                return imageIcon;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /*
    Pobieranie pliku txt z pamieci lub z wykorzystaniem Weak References
     */
    public ArrayList<String> loadRecord(String directoryName) {

        File file;
        FileContainer fileContainer;
        ArrayList<String> arr;

        try {
            if (!weakHashMap.containsKey(directoryName+"\\"+"record.txt")) {
                file = new File(filePath.getAbsolutePath() + "\\" + directoryName+"\\"+"record.txt");
                fileContainer = new FileContainer();
                arr=CreateText(file);
                fileContainer.setReadList(arr);
                weakHashMap.put(directoryName+"\\"+"record.txt",fileContainer);
                fileLoadedFromInfoLabel.setText("Loaded from file");
                return arr;
            } else {
                fileLoadedFromInfoLabel.setText("Loaded using Weak Reference");
                arr= weakHashMap.get(directoryName+"\\"+"record.txt").getReadList();
                return arr;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /*
     Funkcja przeksztalca file na ImageIcon, skaluje obraz
     */
    public ImageIcon createIcon(File file) {
        try {
            Image img = ImageIO.read(file);

            if (img.getWidth(null) >= img.getHeight(null)) {
                int newHeight = labelForImage.getWidth() * img.getHeight(null) / img.getWidth(null);
                Image dimg = img.getScaledInstance(labelForImage.getWidth(), newHeight, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(dimg);
                return icon;
            } else {
                int newWidth = labelForImage.getHeight() * img.getWidth(null) / img.getHeight(null);
                Image dimg = img.getScaledInstance(newWidth, labelForImage.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(dimg);
                return icon;
            }

        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "Nie mo¿na otworzyæ pliku.");
            e1.printStackTrace();
        }
        return null;
    }

    /*
    Funkcja odczytuje tekst z pliku i zwraca go w postaci ArrayList
     */
    public ArrayList<String> CreateText(File file) {
        ArrayList<String> readedList = new ArrayList<>();
        String line;

        BufferedReader bf;
        try {
            bf = new BufferedReader(new FileReader(file));
            while((line = bf.readLine())!=null)
                readedList.add(line);
            bf.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return readedList;
    }
}
