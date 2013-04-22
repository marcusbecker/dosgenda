/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.dosgenda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author mbecker
 */
public class App {

    private static List<Contato> contatos = new ArrayList<>(30);
    private static final File arquivo = new File("dosgenda.txt");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //carrega automaticamente ao abrir
        String contatosSalvos = carregarArquivo();
        contatos.addAll(converteTextoParaContato(contatosSalvos));

        Scanner sc = new Scanner(System.in);//Le comandos
        while (sc.hasNext()) {//A cada nova linha, executa o loop
            String comando = sc.nextLine();

            if (comando.startsWith("add")) {//adicionar
                if (comando.indexOf(",") != -1) {
                    String texto = comando.substring("add".length()).trim();

                    Contato novoContato = new Contato(texto.substring(0,
                            texto.indexOf(",")).trim(), texto.substring(texto.indexOf(",") + 1).trim());
                    contatos.add(novoContato);

                    System.out.println("Contatos adicionado com sucesso.");

                } else {
                    System.out.println("Por favor digitar: 'nome' + ',' + 'telefone'.");
                }

            } else if (comando.equalsIgnoreCase("lst")) {//listagem
                System.out.println("Listando contatos:");
                for (Contato c : contatos) {
                    System.out.println(c.getNome() + "\t| " + c.getTelefone());
                }

            } else if (comando.startsWith("bsc")) {//busca
                String texto = comando.substring("bsc".length()).trim();
                System.out.println("Buscando contatos:");
                for (Contato c : contatos) {
                    if (c.getNome().toLowerCase().startsWith(texto.toLowerCase()) || 
                            c.getTelefone().startsWith(texto)) {
                        System.out.println(c.getNome() + "\t|\t" + c.getTelefone());
                    }
                }
            } else if (comando.startsWith("exc")) {//excluir
                String texto = comando.substring("exc".length()).trim();
                Contato remover = null;

                for (Contato c : contatos) {
                    if (c.getNome().equalsIgnoreCase(texto) || c.getTelefone().equalsIgnoreCase(texto)) {
                        remover = c;
                        break;
                    }
                }

                if (remover != null) {
                    contatos.remove(remover);
                    System.out.println("Contato: " + remover.getNome() + ", " + remover.getTelefone() + " excluido!");
                }else{
                    System.out.println("Nenhum conato excluido.");
                }

            } else if (comando.equalsIgnoreCase("sair")) {
                //salva automaticamente ao sair
                String texto = converteContatoParaTexto(contatos);
                salvarArquivo(texto);

                System.exit(0);

            } else {
                System.out.println("Comando inválido.");
                System.out.println("Utilizar: add, lst, bsc ou exc.");
            }
        }
    }

    public static String carregarArquivo() {
        StringBuilder conteudo = new StringBuilder(500);

        try {
            if (arquivo.exists()) {
                InputStreamReader is = new InputStreamReader(new FileInputStream(arquivo), Charset.forName("iso-8859-1"));
                BufferedReader reader = new BufferedReader(is);

                String linha = null;
                while ((linha = reader.readLine()) != null) {
                    conteudo.append(linha).append(System.getProperty("line.separator"));
                }
                reader.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ex.getMessage());


        } catch (IOException ex) {
            Logger.getLogger(ex.getMessage());
        }

        return conteudo.toString();
    }

    public static void salvarArquivo(String conteudo) {

        OutputStreamWriter osw = null;

        try {
            osw = new OutputStreamWriter(new FileOutputStream(arquivo), Charset.forName("iso-8859-1"));
            osw.write(conteudo);

        } catch (IOException ex) {
            Logger.getLogger(ex.getMessage());
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ex.getMessage());
            }
        }
    }

    public static List<Contato> converteTextoParaContato(String conteudo) {
        List<Contato> lst = new ArrayList<>(10);

        Scanner sc = new Scanner(conteudo);//Separa contatos por linha
        while (sc.hasNext()) {
            String[] arr = sc.nextLine().split(";");//separa nome e telefone do contato
            if (arr.length == 2) {
                lst.add(new Contato(arr[0], arr[1]));
            }
        }

        return lst;
    }

    public static String converteContatoParaTexto(List<Contato> lst) {
        String rs = "";

        for (Contato c : lst) {
            //Adiciona ao texto o nome, telefone e uma nova linha
            rs += c.getNome() + ";" + c.getTelefone() + System.getProperty("line.separator");
        }

        return rs;
    }
}
