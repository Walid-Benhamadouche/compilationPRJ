import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class analyseur {

    private JFrame frmMonAnalyseur;
    private JTextArea textArea;

    private static JFileChooser file_chooser = new JFileChooser("C:\\Users\\sol\\Desktop");
    private static FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers text", "snl");
    private static ArrayList<String> mots = new ArrayList<>();
    private static ArrayList<String> lignes = new ArrayList<>();
    private static ArrayList<String> sortie_lexic;

    static {
        sortie_lexic = new ArrayList<>();
    }

    private static String[] mot;

    private static void charger() throws FileNotFoundException {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("compila file", "compila");
        file_chooser.setFileFilter(filter);
        file_chooser.addChoosableFileFilter(filter);
        if(file_chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
            File file = file_chooser.getSelectedFile();

            Scanner sc_lignes = new Scanner(file);
            Scanner sc_mots = new Scanner(file);
            mots.clear();
            lignes.clear();
            while(sc_lignes.hasNextLine()){
                lignes.add(sc_lignes.nextLine());
            }
            while(sc_mots.hasNext()){
                mots.add(sc_mots.next());
            }

            sc_mots.close();
            sc_lignes.close();
        }
    }

    private boolean isNum(String chaine, int i) {
        char[] nombre = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
        int j = 0;
        while (j < nombre.length) {
            if (chaine.charAt(i) == nombre[j]) {
                return true;
            }
            j++;
        }

        return false;
    }

    private String num(String chaine) {
        int i = 0;
        int token_pos = 0;
        boolean point_unique = true;
        while (i < chaine.length()) {
            if (isNum(chaine, i)) token_pos++;
            else if(point_unique & chaine.charAt(token_pos)=='.') {
                token_pos++;
                point_unique = false;
            }
            i++;
        }

        if (token_pos == chaine.length() && !chaine.contains(".")) return "Nombre entier";
        else if (token_pos == chaine.length() && !point_unique) return "Nombre reel";
        return null;

    }

    private boolean isChar(String chaine, int i) {
        char[] alphabet = { 'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i',
                'J', 'j', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T',
                't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z' };
        int k = 0;
        while (k < alphabet.length) {
            if (chaine.charAt(i) == alphabet[k]) {
                return true;
            }
            k++;
        }
        return false;

    }

    private String id(String chaine) {
        boolean verifier_Premier = false;
        int token_pos = 0;
        int i;
        if (isChar(chaine, 0)) {
            token_pos++;
            verifier_Premier = true;
        }
        if (verifier_Premier && chaine.length() == 1)
            return "identificateur";

        else if (chaine.length() > 1) {
            i = 1;
            while (i < chaine.length()) {

                if (isChar(chaine, i))
                {token_pos++;
                }
                else if (isNum(chaine, i))
                {token_pos++;
                }
                else if (chaine.charAt(i) == '_') {
                    token_pos++;
                }
                i++;
            }
            if (token_pos == chaine.length())
                return "Identificateur";
        }
        return null;
    }

    private String UL_reserve(String chaine) {

        String[] mot_reserve = {"\"", "<", ">", ",", "Start_Program", "Int_Number", ";;", "Affect", "Real_Number", "If", "--", "Else",
                "Start", "Give",":", "Finish", "ShowMes", "//.", "End_Program","ShowVal","to" };

        String[] Affichage = {"mot reserve pour guillemets",
                "symbol inferieur", "symbol superieur", "caractere reserve virgule",
                "Mot reserve debut du programme", " Mot reserve debut declaration d'un entier",
                "Mot reserve fin instruction", "Mot reserve pour affectation entre variables", " Mot reserve debut declaration d'un Real",
                " Mot reserve pour condition SI", "Mot reserve pour condition", "Mot reserve pour condition SINON", "Debut d'un sous programme",
                "Mot reserve pour affectation", "Mot reserve", "Fin d'un sous programme",
                "Mot reserve pour afficher un message", "Mot reserve pour un commentaire", "Mot reserve Fin du programme","Mot reserve pour afficher une valey",
                "Mot resrver affectation"};
        int i = 0;
        while (i < mot_reserve.length) {
            if (chaine.equals(mot_reserve[i])) {
                return Affichage[i];
            }
            i++;
        }
        return null;
    }

    private String syntax(String chaine){

        if(chaine.equals("Start_Program")) return "Debut du programme";
        else if(chaine.equals("Else")) return "SINON";
        else if(chaine.equals("Start")) return "Debut d'un bloc";
        else if(chaine.equals("Finish")) return "Fin d'un bloc";
        else if(chaine.startsWith("//.")) return "un commentaire";
        else if(chaine.equals("End_Program")) return "Fin du programme";
        else if(chaine.startsWith("ShowMes : \" ") && chaine.endsWith(" \" ;;")) return "Affichage d'un message a l'ecran";
        else if(chaine.startsWith("ShowVal : ") && chaine.endsWith(" ;;")) return "Affichage d'une valeur a l'ecran";
        else if(chaine.contains(" ")) {
            mot = chaine.split(" ");
            int i=0, k=1;

            if(mot[i].equals("Int_Number")){
                i++;
                if(mot[i].equals(":"))
                    i++;
                if(id(mot[i]) != null){
                    i++;
                    while(mot[i].equals(",")){
                        i++;
                        k++;
                        if(id(mot[i]) != null) i++;
                    }
                    if(mot[i].equals(";;")) return "Declaration de "+k+" variables entiers";
                }


            }
            else if(mot[i].equals("Give")){
                i++;
                if(id(mot[i]) != null){
                    i++;
                    if(mot[i].equals(":"))i++;
                    if(Objects.equals(num(mot[i]), "Nombre entier")) {
                        i++;
                        if(mot[i].equals(";;")) return "affectation dune valeur entiere "+mot[i-1];
                    }
                    else if(Objects.equals(num(mot[i]), "Nombre reel")){
                        i++;
                        if(mot[i].equals(";;")) return "affectation dune valeur reel "+mot[i-1];
                    }


                }

            }

            else if(mot[i].equals("Real_Number")){
                i++;
                if(mot[i].equals(":"))
                    i++;
                if(id(mot[i]) != null){
                    i++;
                    while(mot[i].equals(",")){
                        i++;
                        k++;
                        if(id(mot[i]) != null) i++;
                    }
                    if(mot[i].equals(";;")) return "Declaration de "+k+" variables reel";
                }
            }

            else if(mot[i].equals("If")){
                i++;
                if(mot[i].equals("--")){
                    i++;
                    if(id(mot[i]) != null){
                        i++;
                        if(mot[i].equals("<") || mot[i].equals(">") || mot[i].equals("==")){
                            i++;
                            if(id(mot[i]) != null){
                                i++;
                                if(mot[i].equals("--")) return "condition";
                            }}}}
            }



            else if(mot[i].equals("Affect")){
                i++;
                if(id(mot[i]) != null){
                    i++;
                    if(mot[i].equals("to")){
                        i++;
                        if(id(mot[i]) != null) {
                            i++;

                            if(mot[i].equals(";;")) return "affectation de "+mot[i-3]+" a "+mot[i-1];
                        }

                    }

                }

            }
        }
        return "erreur de syntaxe";
    }

    private void lexicale() {
        int i = 0;

        while (i < mots.size()) {
            if (UL_reserve(mots.get(i)) != null) {
                sortie_lexic.add(UL_reserve(mots.get(i)));
            } else if (id(mots.get(i)) != null) {
                sortie_lexic.add(id(mots.get(i)));
            } else if (num(mots.get(i)) != null) {
                sortie_lexic.add(num(mots.get(i)));
            }
            else sortie_lexic.add("Erreur");

            i++;
        }

    }

    private String semantique(String chaine){
        if(chaine.equals("Start_Program")) return "public static void main(String[] args) {";
        else if(chaine.equals("End_Program")) return "}";
        else if(chaine.equals("Else")) return "else";
        else if(chaine.equals("Start")) return "{";
        else if(chaine.equals("Finish")) return "}";
        else if(chaine.startsWith("//.")) return "/*ceci est un commentaire*/";

        else if(chaine.startsWith("ShowMes : \" ") && chaine.endsWith(" \" ;;")) return "System.out.println(\"Affichage d'un message a l'ecran\");";
        else if(chaine.startsWith("ShowVal : ") && chaine.endsWith(" ;;")) return "System.out.println(\"Affichage d'une valeur a l'ecran\");";
        else if(chaine.contains(" ")) {
            mot = chaine.split(" ");
            int i = 0, k = 1;

            if (mot[i].equals("Int_Number")) {
                i++;
                if (mot[i].equals(":"))
                    i++;
                if (id(mot[i]) != null) {
                    i++;
                    while (mot[i].equals(",")) {
                        i++;
                        k++;
                        if (id(mot[i]) != null) i++;
                    }
                    if (mot[i].equals(";;"))
                        return "int" + " " + mot[i - 7] + "," + mot[i - 5] + "," + mot[i - 3] + "," + mot[i - 1] + ";";
                }


            } else if (mot[i].equals("Give")) {
                i++;
                if (id(mot[i]) != null) {
                    i++;
                    if (mot[i].equals(":")) i++;
                    if (Objects.equals(num(mot[i]), "Nombre entier")) {
                        i++;
                        if (mot[i].equals(";;")) return mot[i - 2] + "=" + mot[i - 1] + ";";
                    } else if (Objects.equals(num(mot[i]), "Nombre reel")) {
                        i++;
                        if (mot[i].equals(";;")) return mot[i - 3] + "=" + mot[i - 1] + ";";
                    }


                }

            } else if (mot[i].equals("Real_Number")) {
                i++;
                if (mot[i].equals(":"))
                    i++;
                if (id(mot[i]) != null) {
                    i++;
                    while (mot[i].equals(",")) {
                        i++;
                        k++;
                        if (id(mot[i]) != null) i++;
                    }
                    if (mot[i].equals(";;")) return "float " + mot[i - 1] + ";";
                }
            } else if (mot[i].equals("If")) {
                i++;
                if (mot[i].equals("--")) {
                    i++;
                    if (id(mot[i]) != null) {
                        i++;
                        if (mot[i].equals("<") || mot[i].equals(">") || mot[i].equals("==")) {
                            i++;
                            if (id(mot[i]) != null) {
                                i++;
                                if (mot[i].equals("--")) return "if" + "(" + mot[i - 3] + mot[i - 2] + mot[i - 1] + ")";
                            }
                        }
                    }
                }
            } else if (mot[i].equals("Affect")) {
                i++;
                if (id(mot[i]) != null) {
                    i++;
                    if (mot[i].equals("to")) {
                        i++;
                        if (id(mot[i]) != null) {
                            i++;

                            if (mot[i].equals(";;")) return mot[i - 3] + "=" + mot[i - 1] + ";";
                        }

                    }

                }

            } else if (mot[i].equals("Get")) {
                i++;
                if (id(mot[i]) != null) {
                    i++;
                    if (mot[i].equals("From")) {
                        i++;
                        if (id(mot[i]) != null) {
                            i++;
                            if (mot[i].equals("%.")) return mot[i - 3] + "=" + mot[i - 1] + ";";
                        }

                    }

                }


            }
        }
        return "erreur symantique";

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                analyseur window = new analyseur();
                window.frmMonAnalyseur.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private analyseur() {
        initialize();
    }

    private void initialize() {
        frmMonAnalyseur = new JFrame();
        frmMonAnalyseur.setResizable(false);
        frmMonAnalyseur.setTitle("Analyseur lexicale , syntaxique et Semantique");
        frmMonAnalyseur.getContentPane().setBackground(Color.BLACK);
        frmMonAnalyseur.getContentPane().setLayout(null);
        frmMonAnalyseur.setLocationRelativeTo(null);

        Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setBounds(10, 38, 218, 360);
        frmMonAnalyseur.getContentPane().add(panel);

        JButton btnNewButton = new JButton("Charger");
        btnNewButton.setCursor(cursor);
        btnNewButton.setBounds(10, 81, 195, 59);
        btnNewButton.addActionListener(e -> {

            try {
                textArea.setText("");
                charger();


                int i = 0;
                while (i < lignes.size()) {
                    textArea.setText(textArea.getText()+lignes.get(i)+"\n");
                    i++;}
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

        });
        panel.setLayout(null);
        btnNewButton.setForeground(Color.BLACK);
        btnNewButton.setBackground(UIManager.getColor("Button.foreground"));
        btnNewButton.setFont(new Font("Roboto", Font.BOLD, 17));
        panel.add(btnNewButton);

        JButton btnAlexicale = new JButton("Analyse Lexicale");
        btnAlexicale.setCursor(cursor);
        btnAlexicale.setBounds(10, 151, 195, 59);
        btnAlexicale.addActionListener(e -> {
            textArea.setText("");
            lexicale();
            int i = 0;
            while (i < mots.size()) {
                textArea.setText(textArea.getText()+mots.get(i) + " -:- " + sortie_lexic.get(i)+"\n");
                i++;}
        });
        btnAlexicale.setForeground(Color.BLACK);
        btnAlexicale.setBackground(UIManager.getColor("Button.foreground"));
        btnAlexicale.setFont(new Font("Roboto", Font.BOLD, 17));
        panel.add(btnAlexicale);

        JButton btnAsyntaxique = new JButton("Analyse Syntaxique");
        btnAsyntaxique.setCursor(cursor);
        btnAsyntaxique.setBounds(10, 221, 195, 59);
        btnAsyntaxique.addActionListener(e -> {
            textArea.setText("");
            int i = 0;
            while (i < lignes.size()) {
                textArea.setText(textArea.getText()+lignes.get(i) + " -:- " +syntax(lignes.get(i))+"\n");
                i++;}
        });
        btnAsyntaxique.setForeground(Color.BLACK);
        btnAsyntaxique.setBackground(UIManager.getColor("Button.foreground"));
        btnAsyntaxique.setFont(new Font("Roboto", Font.BOLD, 17));
        panel.add(btnAsyntaxique);

        JButton btnAsmantique = new JButton("Analyse Semantique");
        btnAsmantique.setCursor(cursor);
        btnAsmantique.setBounds(10, 292, 195, 59);
        btnAsmantique.addActionListener(e -> {
            textArea.setText("");
            int i = 0;


            while (i < lignes.size()) {
                textArea.setText(textArea.getText()+lignes.get(i) + " -:- " +semantique(lignes.get(i))+"\n");

                i++;}

        });
        btnAsmantique.setForeground(Color.BLACK);
        btnAsmantique.setBackground(UIManager.getColor("Button.foreground"));
        btnAsmantique.setFont(new Font("Roboto", Font.BOLD, 17));
        panel.add(btnAsmantique);

        JLabel lblNewLabel = new JLabel("Commandes :");
        lblNewLabel.setBounds(10, 11, 195, 59);
        lblNewLabel.setForeground(Color.BLACK);
        lblNewLabel.setFont(new Font("Roboto", Font.BOLD, 25));
        panel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Sortie   :");
        lblNewLabel_1.setForeground(Color.WHITE);
        lblNewLabel_1.setFont(new Font("Book Antiqua", Font.BOLD, 20));
        lblNewLabel_1.setBounds(238, 0, 93, 48);
        frmMonAnalyseur.getContentPane().add(lblNewLabel_1);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel_1.setBounds(233, 38, 289, 360);
        frmMonAnalyseur.getContentPane().add(panel_1);
        panel_1.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 11, 269, 338);
        panel_1.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);
        textArea.setForeground(Color.BLACK);
        textArea.setBackground(Color.WHITE);
        textArea.setFont(new Font("Perpetua", Font.BOLD, 16));
        frmMonAnalyseur.setBounds(100, 100, 535, 438);
        frmMonAnalyseur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

