package br.sci.appsondavalidation.model.dataloader;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import br.sci.appsondavalidation.model.dataloader.exception.LoaderException;

/**
 * 
 * @version 1.0.0
 * @author Rafael Carvalho Chagas
 * @since Copyright 2012-2013
 *
 */

public class Loader {
	private List<Object> rawData = new ArrayList<>();
	private int numberOfColumns, numberOfRows;
	private double[] temp, lines;
	private String line = null;
			
	public double[][] data, limits;
	public int[][] code;
	
	private final double d0  = 0.006918;
    private final double dc1 = 0.399912;
    private final double dc2 = 0.006758;
    private final double dc3 = 0.002697;
    private final double ds1 = 0.070257;
    private final double ds2 = 0.000907;
    private final double ds3 = 0.001480;
    private final double et0 = 0.000075;
    private final double tc1 = 0.001868;
    private final double tc2 = 0.014615;
    private final double ts1 = 0.032077;
    private final double ts2 = 0.040890;
    
    private double u0;
    private double zenith_angle;
    private double day_angle;
    private double dec;
    private double eqtime;
    private double tcorr;
    private double hour_angle;
	
    private final double CDR = Math.PI / 180;
    private double num;
    private double dia_jul;
    private double horacorr;
    private double div;
          
	private int cont_gl1n= 0, cont_gl2n= 0, cont_gl3n= 0, cont_glna= 0, cont_glv= 0;
	private int cont_di1n= 0, cont_di2n= 0, cont_di3n= 0, cont_dina= 0, cont_div= 0;
	private int cont_df1n= 0, cont_df2n= 0, cont_df3n= 0, cont_dfna= 0, cont_dfv= 0;
	private int cont_lw1n= 0, cont_lw2n= 0, cont_lw3n= 0, cont_lwna= 0, cont_lwv= 0;
	private int cont_pa1n= 0, cont_pa2n= 0, cont_pa3n= 0, cont_pana= 0, cont_pav= 0;
	private int cont_lx1n= 0, cont_lx2n= 0, cont_lx3n= 0, cont_lxna= 0, cont_lxv= 0;
	private int cont_tp1n= 0, cont_tp2n= 0, cont_tp3n= 0, cont_tpna= 0, cont_tpv= 0;
	private int cont_hu1n= 0, cont_huna= 0, cont_huv= 0;
	private int cont_ps1n= 0, cont_ps2n= 0, cont_psna= 0, cont_psv= 0;
	private int cont_pc1n= 0, cont_pc2n= 0, cont_pc3n= 0, cont_pcna= 0, cont_pcv= 0;
	private int cont_ws1n= 0, cont_ws2n= 0, cont_ws3n= 0, cont_wsna= 0, cont_wsv= 0;
	private int cont_wd1n= 0, cont_wd2n= 0, cont_wd3n= 0, cont_wdna= 0, cont_wdv= 0;
	
	private int cont_vgl = 0;
	private int cont_nagl = 0;
	private int cont_vdi = 0;
	private int cont_nadi = 0;
	private int cont_vdf = 0;
	private int cont_nadf = 0;
	private int cont_vlw = 0;
	private int cont_nalw = 0;
	private int cont_vpa = 0;
	private int cont_napa = 0;
	private int cont_vlx = 0;
	private int cont_nalx = 0;
	
	private int flag_gl= 0;
	private int flag_di= 0;
	private int flag_df= 0;
	private int flag_lw= 0;
	private int flag_pa= 0;
	private int flag_lx= 0;
	private int flag_tp= 0;
	private int flag_hu= 0;
	private int flag_ps= 0;
	private int flag_pc= 0;
	private int flag_ws= 0;
	private int flag_wd= 0;
	
	private double med_gl1n, med_gl2n, med_gl3n, med_glna, med_glv;
	private double med_di1n, med_di2n, med_di3n, med_dina, med_div;
	private double med_df1n, med_df2n, med_df3n, med_dfna, med_dfv;
	private double med_lw1n, med_lw2n, med_lw3n, med_lwna, med_lwv;
	private double med_pa1n, med_pa2n, med_pa3n, med_pana, med_pav;
	private double med_lx1n, med_lx2n, med_lx3n, med_lxna, med_lxv;
	private double med_tp1n, med_tp2n, med_tp3n, med_tpna, med_tpv;
	private double med_hu1n, med_huna, med_huv;
	private double med_ps1n, med_ps2n, med_psna, med_psv;
	private double med_pc1n, med_pc2n, med_pc3n, med_pcna, med_pcv;
	private double med_ws1n, med_ws2n, med_ws3n, med_wsna, med_wsv;
	private double med_wd1n, med_wd2n, med_wd3n, med_wdna, med_wdv;
							
	public Loader() {
		super();
	}
	
	public void extractLines(String input, String output, String id) throws LoaderException {
		try (BufferedReader in = new BufferedReader(new FileReader(input))) {
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output)))) {
				while ((line = in.readLine()) != null) {
					if (line.substring(0, 3).equals(id)) {
						out.print(line);
						out.print("\n");
					}
				}
				in.close();
				out.close();
			}
		} catch (IOException ex) {
			throw new LoaderException(ex);
		}
	}
	
	public void read(String input) throws LoaderException {
		try (BufferedReader in = new BufferedReader(new FileReader(input))) {
			while ((line = in.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, ",");
				lines = new double[token.countTokens()];
				
				if (numberOfColumns <= 0) {
					numberOfColumns = token.countTokens();
				}
				
				for (int i= 0; i< lines.length; i++) {
					lines[i] = Double.parseDouble(token.nextToken());
				}
				rawData.add(lines);
			}
			numberOfRows = rawData.size();
			in.close();
		} catch (IOException ex) {
			throw new LoaderException("Erro Durante a Leitura do Arquivo: " + input, ex);
		} catch (NumberFormatException ex) {
			throw new LoaderException("Arquivo com Problema de Formatação: " + input, ex);
		}
	}
	
	public void writeData(String output, double[][] data, String id) throws LoaderException {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output)))) {
			for (int i= 0; i< data.length; i++) {
				
				// Cabe�alho				
				out.printf("%s;", id);
				out.printf("%.0f;", data[i][1]);
				out.printf("%.0f;", data[i][2]);
				out.printf("%.0f;", data[i][3]);
				
				// Radiação Global
				if ((data[i][4] == 3333) || (data[i][4] == -6999))
					out.print("N/A;");
				else if (data[i][4] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.3f;", data[i][4]);
				
				// Radiação Direta
				if ((data[i][28] == 3333) || (data[i][28] == -6999))
					out.print("N/A;");
				else if (data[i][28] == -5555)
					out.print("N/S;");
				else if (data[i][28] == 0)
					out.printf(Locale.US, "%.0f;", data[i][28]);
				else
					out.printf(Locale.US, "%.3f;", data[i][28]);
				
				// Radiação Difusa
				if ((data[i][8] == 3333) || (data[i][8] == -6999))
					out.print("N/A;");
				else if (data[i][8] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.3f;", data[i][8]);
				
				// Onda Longa
				if ((data[i][32] == 3333) || (data[i][32] == -6999))
					out.print("N/A;");
				else if (data[i][32] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.1f;", data[i][32]);
				
				// Par
				if ((data[i][12] == 3333) || (data[i][12] == -6999))
					out.print("N/A;");
				else if (data[i][12] == -5555)
					out.print("N/S;");
				else if (data[i][12] == 0)
					out.printf(Locale.US, "%.0f;", data[i][12]);
				else
					out.printf(Locale.US, "%.3f;", data[i][12]);
				
				// Lux
				if ((data[i][16] == 3333) || (data[i][16] == -6999))
					out.print("N/A;");
				else if (data[i][16] == -5555)
					out.print("N/S;");
				else if (data[i][16] == 0)
					out.printf(Locale.US, "%.0f;", data[i][16]);
				else
					out.printf(Locale.US, "%.3f;", data[i][16]);
				
				// Temperatura
				if (data[i][20] == 3333)
					out.print("N/A;");
				else if (data[i][20] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.2f;", data[i][20]);
				
				// Umidade
				if (data[i][21] == 3333)
					out.print("N/A;");
				else if (data[i][21] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.2f;", data[i][21]);
				
				// Pressão
				if (data[i][22] == 3333)
					out.print("N/A;");
				else if (data[i][22] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.2f;", data[i][22]);
				
				// Precipitação
				if (data[i][23] == 3333)
					out.print("N/A;");
				else if (data[i][23] == -5555)
					out.print("N/S;");
				else if (data[i][23] == 0)
					out.printf(Locale.US, "%.0f;", data[i][23]);
				else
					out.printf(Locale.US, "%.2f;", data[i][23]);
				
				// Velocidade
				if (data[i][24] == 3333)
					out.print("N/A;");
				else if (data[i][24] == -5555)
					out.print("N/S;");
				else
					out.printf(Locale.US, "%.3f;", data[i][24]);
				
				// Dire��o
				if (data[i][25] == 3333)
					out.print("N/A\n");
				else if (data[i][25] == -5555)
					out.print("N/S\n");		
				else
					out.printf(Locale.US, "%.1f\n", data[i][25]);				
			}
			out.close();
		} catch (IOException ex) {
			throw new LoaderException("Erro Durante a Escrita do Arquivo: " + output, ex);
		}
	}
	
	public void writeCode(String output, double[][] data, int[][] code, String id) throws LoaderException {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output)))) {
			for (int i= 0; i< data.length; i++) {
				
				// Cabe�alho
				out.printf("%s;", id);
				out.printf("%.0f;", data[i][1]);
				out.printf("%.0f;", data[i][2]);
				out.printf("%.0f;", data[i][3]);
				
				// Radiação Global	
				if (code[i][4] == 999)
					out.printf("%d;", code[i][4]);
				else if (code[i][4] == 599)
					out.printf("%d;", code[i][4]);
				else if (code[i][4] == 529)
					out.printf("%d;", code[i][4]);
				else if (code[i][4] == 299)
					out.printf("%d;", code[i][4]);
				else if (code[i][4] == 552)
					out.printf("%d;", code[i][4]);
				else if ((code[i][4] == 3333) || code[i][4] == -6999)
					out.print("N/A;");
				else if (code[i][4] == -5555)
					out.print("N/S;");
				
				// Radiação Direta
				if (code[i][28] == 999)
					out.printf("%d;", code[i][28]);
				else if (code[i][28] == 599)
					out.printf("%d;", code[i][28]);
				else if (code[i][28] == 529)
					out.printf("%d;", code[i][28]);
				else if (code[i][28] == 299)
					out.printf("%d;", code[i][28]);
				else if (code[i][28] == 552)
					out.printf("%d;", code[i][28]);
				else if ((code[i][28] == 3333) || code[i][28] == -6999)
					out.print("N/A;");
				else if (code[i][28] == -5555)
					out.print("N/S;");
				
				// Radiação Difusa
				if (code[i][8] == 999)
					out.printf("%d;", code[i][8]);
				else if (code[i][8] == 599)
					out.printf("%d;", code[i][8]);
				else if (code[i][8] == 529)
					out.printf("%d;", code[i][8]);
				else if (code[i][8] == 299)
					out.printf("%d;", code[i][8]);
				else if (code[i][8] == 552)
					out.printf("%d;", code[i][8]);
				else if ((code[i][8] == 3333) || code[i][8] == -6999)
					out.print("N/A;");
				else if (code[i][8] == -5555)
					out.print("N/S;");
				
				// Onda Longa
				if (code[i][32] == 999)
					out.printf("%d;", code[i][32]);
				else if (code[i][32] == 599)
					out.printf("%d;", code[i][32]);
				else if (code[i][32] == 529)
					out.printf("%d;", code[i][32]);
				else if (code[i][32] == 299)
					out.printf("%d;", code[i][32]);
				else if (code[i][32] == 552)
					out.printf("%d;", code[i][32]);
				else if ((code[i][32] == 3333) || code[i][32] == -6999)
					out.print("N/A;");
				else if (code[i][32] == -5555)
					out.print("N/S;");
				
				// Par
				if (code[i][12] == 999)
					out.printf("%d;", code[i][12]);
				else if (code[i][12] == 599)
					out.printf("%d;", code[i][12]);
				else if (code[i][12] == 529)
					out.printf("%d;", code[i][12]);
				else if (code[i][12] == 299)
					out.printf("%d;", code[i][12]);
				else if (code[i][12] == 552)
					out.printf("%d;", code[i][12]);
				else if ((code[i][12] == 3333) || code[i][12] == -6999)
					out.print("N/A;");
				else if (code[i][12] == -5555)
					out.print("N/S;");
				
				// Lux
				if (code[i][16] == 999)
					out.printf("%d;", code[i][16]);
				else if (code[i][16] == 599)
					out.printf("%d;", code[i][16]);
				else if (code[i][16] == 529)
					out.printf("%d;", code[i][16]);
				else if (code[i][16] == 299)
					out.printf("%d;", code[i][16]);
				else if (code[i][16] == 552)
					out.printf("%d;", code[i][16]);
				else if ((code[i][16] == 3333) || code[i][16] == -6999)
					out.print("N/A;");
				else if (code[i][16] == -5555)
					out.print("N/S;");
				
				// Temperatura
				if (code[i][20] == 999)
					out.printf("%d;", code[i][20]);
				else if (code[i][20] == 559)
					out.printf("%d;", code[i][20]);
				else if (code[i][20] == 529)
					out.printf("%d;", code[i][20]);
				else if (code[i][20] == 299)
					out.printf("%d;", code[i][20]);
				else if (code[i][20] == 552)
					out.printf("%d;", code[i][20]);
				else if (code[i][20] == 3333)
					out.print("N/A;");
				else if (code[i][20] == -5555)
					out.print("N/S;");
				
				// Umidade
				if (code[i][21] == 9)
					out.printf("00%d;", code[i][21]);
				else if (code[i][21] == 552)
					out.printf("%d;", code[i][21]);
				else if (code[i][21] == 3333)
					out.print("N/A;");
				else if (code[i][21] == -5555)
					out.print("N/S;");
				
				// Pressão
				if (code[i][22] == 99)
					out.printf("0%d;", code[i][22]);
				else if (code[i][22] == 559)
					out.printf("%d;", code[i][22]);
				else if (code[i][22] == 529)
					out.printf("%d;", code[i][22]);
				else if (code[i][22] == 299)
					out.printf("%d;", code[i][22]);
				else if (code[i][22] == 552)
					out.printf("%d;", code[i][22]);
				else if (code[i][22] == 3333)
					out.print("N/A;");
				else if (code[i][22] == -5555)
					out.print("N/S;");
				
				// Precipitação
				if (code[i][23] == 999)
					out.printf("%d;", code[i][23]);
				else if (code[i][23] == 559)
					out.printf("%d;", code[i][23]);
				else if (code[i][23] == 529)
					out.printf("%d;", code[i][23]);
				else if (code[i][23] == 299)
					out.printf("%d;", code[i][23]);
				else if (code[i][23] == 552)
					out.printf("%d;", code[i][23]);
				else if (code[i][23] == 3333)
					out.print("N/A;");
				else if (code[i][23] == -5555)
					out.print("N/S;");
				
				// Velocidade
				if (code[i][24] == 999)
					out.printf("%d;", code[i][24]);
				else if (code[i][24] == 559)
					out.printf("%d;", code[i][24]);
				else if (code[i][24] == 529)
					out.printf("%d;", code[i][24]);
				else if (code[i][24] == 299)
					out.printf("%d;", code[i][24]);
				else if (code[i][24] == 552)
					out.printf("%d;", code[i][24]);
				else if (code[i][24] == 3333)
					out.print("N/A;");
				else if (code[i][24] == -5555)
					out.print("N/S;");
				
				// Dire��o
				if (code[i][25] == 999)
					out.printf("%d\n", code[i][25]);
				else if (code[i][25] == 559)
					out.printf("%d\n", code[i][25]);
				else if (code[i][25] == 529)
					out.printf("%d\n", code[i][25]);
				else if (code[i][25] == 299)
					out.printf("%d\n", code[i][25]);
				else if (code[i][25] == 552)
					out.printf("%d\n", code[i][25]);
				else if (code[i][25] == 3333)
					out.print("N/A\n");
				else if (code[i][25] == -5555)
					out.print("N/S\n");
			}
			out.close();
		} catch (IOException ex) {
			throw new LoaderException("Erro Durante a Escrita do Arquivo: " + output, ex);
		}
	}
	
	public void writeReportData(String output, String station, int year, int month, String id, int[][] code, double latitude, double longitude) throws LoaderException {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output)))) {
			for (int i= 0; i< code.length; i++) {
				
				num = data[i][3];
	            div = num / 60;
	            dia_jul = (int) data[i][2];
	            
	            day_angle = (2 * PI / 365.25 * dia_jul);
	            dec = (d0 - dc1 * cos(day_angle) + ds1 * sin(day_angle) - dc2 * cos(2 * day_angle) + ds2 * sin(2 * day_angle) - dc3 * cos(3 * day_angle) + ds3 * sin(3 * day_angle));
	            eqtime = (et0 + tc1 * cos(day_angle) - ts1 * sin(day_angle) - tc2 * cos(2 * day_angle) - ts2 * sin(2 * day_angle)) * 229.18;
	            tcorr = (eqtime + 4 * (longitude - 0 )) / 60;
	            horacorr = tcorr + div;
	            hour_angle = (12.00 - horacorr) * 15;
	            u0 = sin(dec) * sin(latitude * CDR) + cos(dec) * cos(latitude * CDR) * cos(hour_angle * CDR);
	            zenith_angle = (acos(u0)) * 180/Math.PI;
	            
				
	            if (zenith_angle < 90) {
	            	// Radiação Global
	            	if (code[i][4] == 999)
	            		cont_glv++;
	            	else if (code[i][4] == 599)
	            		cont_glv++;
	            	else if (code[i][4] == 552)
	            		cont_gl1n++;
	            	else if (code[i][4] == 529)
	            		cont_gl2n++;
	            	else if (code[i][4] == 299)
	            		cont_gl3n++;
	            	else if (code[i][4] == -5555)
	            		flag_gl = 1;
	            	else if ((code[i][4] == 3333) || (code[i][4] == -6999))
	            		cont_glna++;
	            	
	            	// Radiação Direta
					if (code[i][28] == 999)
						cont_div++;
					else if (code[i][28] == 599)
						cont_div++;
					else if (code[i][28] == 552)
						cont_di1n++;
					else if (code[i][28] == 529)
						cont_di2n++;
					else if (code[i][28] == 299)
						cont_di3n++;
					else if (code[i][28] == -5555)
						flag_di = 1;
					else if ((code[i][28] == 3333) || (code[i][28] == -6999))
						cont_dina++;
					
					// Radiação Difusa
					if (code[i][8] == 999)
						cont_dfv++;
					else if (code[i][8] == 599)
						cont_dfv++;
					else if (code[i][8] == 552)
						cont_df1n++;
					else if (code[i][8] == 529)
						cont_df2n++;
					else if (code[i][8] == 299)
						cont_df3n++;
					else if (code[i][8] == -5555)
						flag_df = 1;
					else if ((code[i][8] == 3333) || (code[i][8] == -6999))
						cont_dfna++;
					
					
					// Radiação Onda Longa
					if (code[i][32] == 999)
						cont_lwv++;
					else if (code[i][32] == 599)
						cont_lwv++;
					else if (code[i][32] == 552)
						cont_lw1n++;
					else if (code[i][32] == 529)
						cont_lw2n++;
					else if (code[i][32] == 299)
						cont_lw3n++;
					else if (code[i][32] == -5555)
						flag_lw = 1;
					else if ((code[i][32] == 3333) || (code[i][32] == -6999))
						cont_lwna++;
					
					
					// Radiação Par
					if (code[i][12] == 999)
						cont_pav++;
					else if (code[i][12] == 599)
						cont_pav++;
					else if (code[i][12] == 552)
						cont_pa1n++;
					else if (code[i][12] == 529)
						cont_pa2n++;
					else if (code[i][12] == 299)
						cont_pa3n++;
					else if (code[i][12] == -5555)
						flag_pa = 1;
					else if ((code[i][12] == 3333) || (code[i][12] == -6999))
						cont_pana++;
					
					// Radiação Lux
					if (code[i][16] == 999)
						cont_lxv++;
					else if (code[i][16] == 599)
						cont_lxv++;
					else if (code[i][16] == 552)
						cont_lx1n++;
					else if (code[i][16] == 529)
						cont_lx2n++;
					else if (code[i][16] == 299)
						cont_lx3n++;
					else if (code[i][16] == -5555)
						flag_lx = 1;
					else if ((code[i][16] == 3333) || (code[i][16] == -6999))
						cont_lxna++;
	            }
	            
	            if (zenith_angle > 90) {
	            	
	            	// Radiação Global 
	            	if ((code[i][4] == 3333) || (code[i][4] == -6999)) {
	            		cont_nagl++;
	            	}
	            	
	            	if ((code[i][4] != 3333) && (code[i][4] != -6999)) {
	            		cont_vgl++;                            
	            	}	
	            	
	            	// Radiação Direta
	            	if ((code[i][28] == 3333) || (code[i][28] == -6999)) {
	            		cont_nadi++;
	            	}
	            	
	            	if ((code[i][28] != 3333) && (code[i][28] != -6999)) {
	            		cont_vdi++;
	            	}
	            	
	            	// Radiação Difusa
	            	if ((code[i][8] == 3333) || (code[i][8] == -6999)) {
	            		cont_nadf++;
	            	}
	            	
	            	if ((code[i][8] != 3333) && (code[i][8] != -6999)) {
	            		cont_vdf++;
	            	}
	            	
	            	// Radiação Onda Longa
	            	if ((code[i][32] == 3333) || (code[i][32] == -6999)) {
	            		cont_nalw++;
	            	}
	            	
	            	if ((code[i][8] != 3333) && (code[i][8] != -6999)) {
	            		cont_vlw++;
	            	}
	            	
	            	// Radiação Par
	            	if ((code[i][12] == 3333) || (code[i][12] == -6999)) {
	            		cont_napa++;
	            	}
	            	
	            	if ((code[i][12] != 3333) && (code[i][12] != -6999)) {
	            		cont_vpa++;
	            	}
	            		            	
	            	// Radiação Lux
	            	if ((code[i][16] == 3333) || (code[i][16] == -6999)) {
	            		cont_nalx++;
	            	}
	            	
	            	if ((code[i][16] != 3333) && (code[i][16] != -6999)) {
	            		cont_vlx++;
	            	}
	            }
	            	                        
				// Temperatura do Ar
				if (code[i][20] == 999)
					cont_tpv++;
				else if (code[i][20] == 559)
					cont_tpv++;
				else if (code[i][20] == 552)
					cont_tp1n++;
				else if (code[i][20] == 529)
					cont_tp2n++;
				else if (code[i][20] == 299)
					cont_tp3n++;
				else if (code[i][20] == -5555)
					flag_tp = 1;
				else if (code[i][20] == 3333)
					cont_tpna++;
				
				// Umidade Relativa do Ar
				if (code[i][21] == 9)
					cont_huv++;
				else if (code[i][21] == 552)
					cont_hu1n++;
				else if (code[i][21] == -5555)
					flag_hu = 1;
				else if (code[i][21] == 3333)
					cont_huna++;
				
				// Pressão Atmosf�rica
				if (code[i][22] == 99)
					cont_psv++;
				else if (code[i][22] == 559)
					cont_psv++;
				else if (code[i][22] == 552)
					cont_ps1n++;
				else if (code[i][22] == 529)
					cont_ps2n++;
				else if (code[i][22] == -5555)
					flag_ps = 1;
				else if (code[i][22] == 3333)
					cont_psna++;
				
				// Precipitação Acumulada
				if (code[i][23] == 999)
					cont_pcv++;
				else if (code[i][23] == 559)
					cont_pcv++;
				else if (code[i][23] == 552)
					cont_pc1n++;
				else if (code[i][23] == 529)
					cont_pc2n++;
				else if (code[i][23] == 299)
					cont_pc3n++;
				else if (code[i][23] == -5555)
					flag_pc = 1;
				else if (code[i][23] == 3333)
					cont_pcna++;
				
				// Velocidade do Vento
				if (code[i][24] == 999)
					cont_wsv++;
				else if (code[i][24] == 559)
					cont_wsv++;
				else if (code[i][24] == 552)
					cont_ws1n++;
				else if (code[i][24] == 529)
					cont_ws2n++;
				else if (code[i][24] == 299)
					cont_ws3n++;
				else if (code[i][24] == -5555)
					flag_ws = 1;
				else if (code[i][24] == 3333)
					cont_wsna++;
				
				// Dire��o do Vento
				if (code[i][25] == 999)
					cont_wdv++;
				else if (code[i][25] == 559)
					cont_wdv++;
				else if (code[i][25] == 552)
					cont_wd1n++;
				else if (code[i][25] == 529)
					cont_wd2n++;
				else if (code[i][25] == 299)
					cont_wd3n++;
				else if (code[i][25] == -5555)
					flag_wd = 1;
				else if (code[i][25] == 3333)
					cont_wdna++;								
			}
			
			// Percentuais Radiação Global 
			med_gl1n = (double)(cont_gl1n * 100) / numberOfRows;
			med_gl2n = (double)(cont_gl2n * 100) / numberOfRows;
			med_gl3n = (double)(cont_gl3n * 100) / numberOfRows;
			med_glna = (double)((cont_glna + cont_nagl) * 100) / numberOfRows;
			med_glv  = (double)((cont_glv + cont_vgl) * 100)  / numberOfRows;
			
			// Percentuais Radiação Direta
			med_di1n = (double)(cont_di1n * 100) / numberOfRows;
			med_di2n = (double)(cont_di2n * 100) / numberOfRows;
			med_di3n = (double)(cont_di3n * 100) / numberOfRows;
			med_dina = (double)((cont_dina + cont_nadi) * 100) / numberOfRows;
			med_div  = (double)((cont_div + cont_vdi) * 100)  / numberOfRows;
			
			// Percentuais Radiação Difusa
			med_df1n = (double)(cont_df1n * 100) / numberOfRows;
			med_df2n = (double)(cont_df2n * 100) / numberOfRows;
			med_df3n = (double)(cont_df3n * 100) / numberOfRows;
			med_dfna = (double)((cont_dfna + cont_nadf) * 100) / numberOfRows;
			med_dfv  = (double)((cont_dfv + cont_vdf) * 100)  / numberOfRows;
				
			// Percentuais Radiação Onda Longa
			med_lw1n = (double)(cont_lw1n * 100) / numberOfRows;
			med_lw2n = (double)(cont_lw2n * 100) / numberOfRows;
			med_lw3n = (double)(cont_lw3n * 100) / numberOfRows;
			med_lwna = (double)((cont_lwna + cont_nalw) * 100) / numberOfRows;
			med_lwv  = (double)((cont_lwv + cont_vlw) * 100)  / numberOfRows;
				
			// Percentuais Radiação Par
			med_pa1n = (double)(cont_pa1n * 100) / numberOfRows;
			med_pa2n = (double)(cont_pa2n * 100) / numberOfRows;
			med_pa3n = (double)(cont_pa3n * 100) / numberOfRows;
			med_pana = (double)((cont_pana + cont_napa) * 100) / numberOfRows;
			med_pav  = (double)((cont_pav + cont_vpa) * 100)  / numberOfRows;
			
			// Percentuais Radiação Lux
			med_lx1n = (double)(cont_lx1n * 100) / numberOfRows;
			med_lx2n = (double)(cont_lx2n * 100) / numberOfRows;
			med_lx3n = (double)(cont_lx3n * 100) / numberOfRows;
			med_lxna = (double)((cont_lxna + cont_nalx) * 100) / numberOfRows;
			med_lxv  = (double)((cont_lxv + cont_vlx) * 100)  / numberOfRows; 
			
			// Percentuais Temperatura do Ar
			med_tp1n = (double)(cont_tp1n * 100) / numberOfRows;
			med_tp2n = (double)(cont_tp2n * 100) / numberOfRows;
			med_tp3n = (double)(cont_tp3n * 100) / numberOfRows;
			med_tpna = (double)(cont_tpna * 100) / numberOfRows;
			med_tpv  = (double)(cont_tpv * 100)  / numberOfRows;
			
			// Percentuais Umidade Relativa do Ar
			med_hu1n = (double)(cont_hu1n * 100) / numberOfRows;
			med_huna = (double)(cont_huna * 100) / numberOfRows;
			med_huv  = (double)(cont_huv * 100)  / numberOfRows;
				
			// Percentuais Pressão Atmosf�rica
			med_ps1n = (double)(cont_ps1n * 100) / numberOfRows;
			med_ps2n = (double)(cont_ps2n * 100) / numberOfRows;
			med_psna = (double)(cont_psna * 100) / numberOfRows;
			med_psv  = (double)(cont_psv * 100)  / numberOfRows;
			
			// Percentuais Precipitação Acumulada
			med_pc1n = (double)(cont_pc1n * 100) / numberOfRows;
			med_pc2n = (double)(cont_pc2n * 100) / numberOfRows;
			med_pc3n = (double)(cont_pc3n * 100) / numberOfRows;
			med_pcna = (double)(cont_pcna * 100) / numberOfRows;
			med_pcv  = (double)(cont_pcv * 100)  / numberOfRows;
				
			// Percentuais Velocidade do Vento
			med_ws1n = (double)(cont_ws1n * 100) / numberOfRows;
			med_ws2n = (double)(cont_ws2n * 100) / numberOfRows;
			med_ws3n = (double)(cont_ws3n * 100) / numberOfRows;
			med_wsna = (double)(cont_wsna * 100) / numberOfRows;
			med_wsv  = (double)(cont_wsv * 100)  / numberOfRows;
			
			// Percentuais Dire��o do Vento
			med_wd1n = (double)(cont_wd1n * 100) / numberOfRows;
			med_wd2n = (double)(cont_wd2n * 100) / numberOfRows;
			med_wd3n = (double)(cont_wd3n * 100) / numberOfRows;
			med_wdna = (double)(cont_wdna * 100) / numberOfRows;
			med_wdv  = (double)(cont_wdv * 100)  / numberOfRows;
							
			out.print("Valores em Porcentagem dos Dados Suspeitos\n");
			out.print("e ou Reprovados nos Níveis 1, 2 e 3, e Gaps N/A.\n\n\n");
			out.printf("Estação: %s (%s %s)\n", station, output.substring(0, 3), id);
			out.printf("Data: %d-%d\n", month, year);
			out.printf("Qtde total de dados: %d\n\n", numberOfRows);
				
			if (flag_gl == 0) {
				out.printf(Locale.US, "Global Nível 1 = %.1f%%\n", med_gl1n);
				out.printf(Locale.US, "Global Nível 2 = %.1f%%\n", med_gl2n);
				out.printf(Locale.US, "Global Nível 3 = %.1f%%\n", med_gl3n);
				out.printf(Locale.US, "Global Válido  = %.1f%%\n", med_glv);
				out.printf(Locale.US, "Global N/A     = %.1f%%\n\n", med_glna);
			} else {
				out.print("Global Nível 1  = N/S\n");
				out.print("Global Nível 2  = N/S\n");
				out.print("Global Nível 3  = N/S\n");
				out.print("Global Válido   = N/S\n");
				out.print("Global N/A      = N/S\n\n");				
			}
				
			if (flag_di == 0) {
				out.printf(Locale.US, "Direta Nível 1 = %.1f%%\n", med_di1n);
				out.printf(Locale.US, "Direta Nível 2 = %.1f%%\n", med_di2n);
				out.printf(Locale.US, "Direta Nível 3 = %.1f%%\n", med_di3n);
				out.printf(Locale.US, "Direta Válido  = %.1f%%\n", med_div);
				out.printf(Locale.US, "Direta N/A     = %.1f%%\n\n", med_dina);
			} else {
				out.print("Direta Nível 1  = N/S\n");
				out.print("Direta Nível 2  = N/S\n");
				out.print("Direta Nível 3  = N/S\n");
				out.print("Direta Válido   = N/S\n");
				out.print("Direta N/A      = N/S\n\n");
			}
				
			if (flag_df == 0) {
				out.printf(Locale.US, "Difusa Nível 1 = %.1f%%\n", med_df1n);
				out.printf(Locale.US, "Difusa Nível 2 = %.1f%%\n", med_df2n);
				out.printf(Locale.US, "Difusa Nível 3 = %.1f%%\n", med_df3n);
				out.printf(Locale.US, "Difusa Válido  = %.1f%%\n", med_dfv);
				out.printf(Locale.US, "Difusa N/A     = %.1f%%\n\n", med_dfna);
			} else {
				out.print("Difusa Nível 1  = N/S\n");
				out.print("Difusa Nível 2  = N/S\n");
				out.print("Difusa Nível 3  = N/S\n");
				out.print("Difusa Válido   = N/S\n");
				out.print("Difusa N/A      = N/S\n\n");
			}
				
			if (flag_lw == 0) {
				out.printf(Locale.US, "Onda Longa Nível 1 = %.1f%%\n", med_lw1n);
				out.printf(Locale.US, "Onda Longa Nível 2 = %.1f%%\n", med_lw2n);
				out.printf(Locale.US, "Onda Longa Nível 3 = %.1f%%\n", med_lw3n);
				out.printf(Locale.US, "Onda Longa Válido  = %.1f%%\n", med_lwv);
				out.printf(Locale.US, "Onda Longa N/A     = %.1f%%\n\n", med_lwna);
			} else {
				out.print("Onda Longa Nível 1  = N/S\n");
				out.print("Onda Longa Nível 2  = N/S\n");
				out.print("Onda Longa Nível 3  = N/S\n");
				out.print("Onda Longa Válido   = N/S\n");
				out.print("Onda Longa N/A      = N/S\n\n");
			}
				
			if (flag_pa == 0) {
				out.printf(Locale.US, "Par Nível 1 = %.1f%%\n", med_pa1n);
				out.printf(Locale.US, "Par Nível 2 = %.1f%%\n", med_pa2n);
				out.printf(Locale.US, "Par Nível 3 = %.1f%%\n", med_pa3n);
				out.printf(Locale.US, "Par Válido  = %.1f%%\n", med_pav);
				out.printf(Locale.US, "Par N/A     = %.1f%%\n\n", med_pana);				
			} else {
				out.print("Par Nível 1  = N/S\n");
				out.print("Par Nível 2  = N/S\n");
				out.print("Par Nível 3  = N/S\n");
				out.print("Par Válido   = N/S\n");
				out.print("Par N/A      = N/S\n\n");
			}
				
			if (flag_lx == 0) {
				out.printf(Locale.US, "Lux Nível 1 = %.1f%%\n", med_lx1n);
				out.printf(Locale.US, "Lux Nível 2 = %.1f%%\n", med_lx2n);
				out.printf(Locale.US, "Lux Nível 3 = %.1f%%\n", med_lx3n);
				out.printf(Locale.US, "Lux Válido  = %.1f%%\n", med_lxv);
				out.printf(Locale.US, "Lux N/A     = %.1f%%\n\n", med_lxna);				
			} else {
				out.print("Lux Nível 1  = N/S\n");
				out.print("Lux Nível 2  = N/S\n");
				out.print("Lux Nível 3  = N/S\n");
				out.print("Lux Válido   = N/S\n");
				out.print("Lux N/A      = N/S\n\n");
			}
				
			if (flag_tp == 0) {
				out.printf(Locale.US, "Temperatura Nível 1 = %.1f%%\n", med_tp1n);
				out.printf(Locale.US, "Temperatura Nível 2 = %.1f%%\n", med_tp2n);
				out.printf(Locale.US, "Temperatura Nível 3 = %.1f%%\n", med_tp3n);
				out.printf(Locale.US, "Temperatura Válido  = %.1f%%\n", med_tpv);
				out.printf(Locale.US, "Temperatura N/A     = %.1f%%\n\n", med_tpna);
			} else {
				out.print("Temperatura Nível 1  = N/S\n");
				out.print("Temperatura Nível 2  = N/S\n");
				out.print("Temperatura Nível 3  = N/S\n");
				out.print("Temperatura Válido   = N/S\n");
				out.print("Temperatura N/A      = N/S\n\n");
			}
				
			if (flag_hu == 0) {
				out.printf(Locale.US, "Umidade Nível 1 = %.1f%%\n", med_hu1n);
				out.printf(Locale.US, "Umidade Válido  = %.1f%%\n", med_huv);
				out.printf(Locale.US, "Umidade N/A     = %.1f%%\n\n", med_huna);				
			} else {
				out.print("Umidade Nível 1  = N/S\n");
				out.print("Umidade Válido   = N/S\n");
				out.print("Umidade N/A      = N/S\n\n");
			}
				
			if (flag_ps == 0) {
				out.printf(Locale.US, "Pressão Nível 1 = %.1f%%\n", med_ps1n);
				out.printf(Locale.US, "Pressão Nível 2 = %.1f%%\n", med_ps2n);
				out.printf(Locale.US, "Pressão Válido  = %.1f%%\n", med_psv);
				out.printf(Locale.US, "Pressão N/A     = %.1f%%\n\n", med_psna);
			} else {
				out.print("Pressão Nível 1  = N/S\n");
				out.print("Pressão Nível 2  = N/S\n");
				out.print("Pressão Válido   = N/S\n");
				out.print("Pressão N/A      = N/S\n\n");
			}
				
			if (flag_pc == 0) {
				out.printf(Locale.US, "Precipitação Nível 1 = %.1f%%\n", med_pc1n);
				out.printf(Locale.US, "Precipitação Nível 2 = %.1f%%\n", med_pc2n);
				out.printf(Locale.US, "Precipitação Nível 3 = %.1f%%\n", med_pc3n);
				out.printf(Locale.US, "Precipitação Válido  = %.1f%%\n", med_pcv);
				out.printf(Locale.US, "Precipitação N/A     = %.1f%%\n\n", med_pcna);				
			} else {
				out.print("Precipitação Nível 1  = N/S\n");
				out.print("Precipitação Nível 2  = N/S\n");
				out.print("Precipitação Nível 3  = N/S\n");
				out.print("Precipitação Válido   = N/S\n");
				out.print("Precipitação N/A      = N/S\n\n");
			}
			
			if (flag_ws == 0) {
				out.printf(Locale.US, "Vel. Vento Nível 1 = %.1f%%\n", med_ws1n);
				out.printf(Locale.US, "Vel. Vento Nível 2 = %.1f%%\n", med_ws2n);
				out.printf(Locale.US, "Vel. Vento Nível 3 = %.1f%%\n", med_ws3n);
				out.printf(Locale.US, "Vel. Vento Válido  = %.1f%%\n", med_wsv);
				out.printf(Locale.US, "Vel. Vento N/A     = %.1f%%\n\n", med_wsna);
			} else {
				out.print("Vel. Vento Nível 1  = N/S\n");
				out.print("Vel. Vento Nível 2  = N/S\n");
				out.print("Vel. Vento Nível 3  = N/S\n");
				out.print("Vel. Vento Válido   = N/S\n");
				out.print("Vel. Vento N/A      = N/S\n\n");
			}
			
			if (flag_wd == 0) {
				out.printf(Locale.US, "Dir. Vento Nível 1 = %.1f%%\n", med_wd1n);
				out.printf(Locale.US, "Dir. Vento Nível 2 = %.1f%%\n", med_wd2n);
				out.printf(Locale.US, "Dir. Vento Nível 3 = %.1f%%\n", med_wd3n);
				out.printf(Locale.US, "Dir. Vento Válido  = %.1f%%\n", med_wdv);
				out.printf(Locale.US, "Dir. Vento N/A     = %.1f%%\n\n", med_wdna);
			} else {
				out.print("Dir. Vento Nível 1  = N/S\n");
				out.print("Dir. Vento Nível 2  = N/S\n");
				out.print("Dir. Vento Nível 3  = N/S\n");
				out.print("Dir. Vento Válido   = N/S\n");
				out.print("Dir. Vento N/A      = N/S\n\n");
			}
			out.close();
		} catch (IOException ex) {
			throw new LoaderException("Erro Durante a Escrita do Arquivo: " + output, ex);
		}
	}
	
	public void buildsMatrixData(String input) throws LoaderException {
		numberOfColumns = 0;
		numberOfRows = 0;
		
		read(input);
		
		data = new double[numberOfRows][numberOfColumns];
		
		try {
		 for (int i= 0; i< numberOfRows; i++) {
			 temp = (double[]) rawData.get(i);
			 System.arraycopy(temp, 0, data[i], 0, numberOfColumns);
		 }
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new LoaderException("Erro ao Acessar Índice da Matriz", ex);
		}
		rawData.clear();
	}
	
	public void buildsMatrixCode(String input) {
		numberOfColumns = 0;
		numberOfRows = 0;
		
		read(input);
		
		code = new int[numberOfRows][numberOfColumns];
		
		try {
		  for (int i= 0; i< numberOfRows; i++) {
			  code[i] = new int[numberOfColumns];
		  }
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new LoaderException("Erro ao Acessar Índice da Matriz", ex);
		}
	}
	
	public void buildsMatrixLimits(String input) throws LoaderException {
		numberOfColumns = 0;
		numberOfRows = 0;
		
		read(input);
		
		limits = new double[numberOfRows][numberOfColumns];
		
		try {
		 for (int i= 0; i< numberOfRows; i++) {
			 temp = (double[]) rawData.get(i);
			 System.arraycopy(temp, 0, limits[i], 0, numberOfColumns);
		 }
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new LoaderException("Erro ao Acessar Índice da Matriz", ex);
		}
		rawData.clear();
	}
	
	public double getTempMax(String input, int station, int month) {
		buildsMatrixLimits(input);
		
		double temp_max = 0;
		
		for (int i= 0; i< numberOfRows; i++) {
			if (limits[i][0] == station) {
				temp_max = limits[i][month];
			}
		}
		return temp_max;
	}
	
	public double getTempMin(String input, int station, int month) {
		buildsMatrixLimits(input);
		
		double temp_min = 0;
		
		for (int i= 0; i< numberOfRows; i++) {
			if (limits[i][0] == station) {
				temp_min = limits[i][month];
			}
		}
		return temp_min;
	}
	
	public double getPresMax(String input, int station) {
		buildsMatrixLimits(input);
		
		double pres_max = 0;
		
		for (int i= 0; i< numberOfRows; i++) {
			if (limits[i][0] == station) {
				pres_max = limits[i][1];
			}
		}
		return pres_max;
	}
	
	public double getPresMin(String input, int station) {
		buildsMatrixLimits(input);
		
		double pres_min = 0;
		
		for (int i= 0; i< numberOfRows; i++) {
			if (limits[i][0] == station) {
				pres_min = limits[i][1];
			}
		}
		return pres_min;
	}
	
	public double getPrecMax(String input, int station, int month) {
		buildsMatrixLimits(input);
		
		double prec_max = 0;
		
		for(int i= 0; i< numberOfRows; i++) {
			if (limits[i][0] == station) {
				prec_max = limits[i][month];
			}
		}
		return prec_max;
	}
	
	public int getCols() {
		return numberOfColumns;
	}
	
	public int getRows() {
		return numberOfRows;
	}
}