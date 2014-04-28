import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JOptionPane;

import br.sci.appsondavalidation.model.controller.ScreeningController;
import br.sci.appsondavalidation.model.dataloader.Loader;
import br.sci.appsondavalidation.model.dataloader.exception.LoaderException;
import br.sci.appsondavalidation.model.entity.InfoData;

public class TesteValidation {
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			args = new String[] { "." };
		}
		
		ext = ".dat";
								
		InfoData infoData = new InfoData();
					 
		File pathNames = new File(args[0]);
						
		FilenameFilter filter = new FilenameFilter() {
				
		@Override
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
		 	if (lowercaseName.endsWith(ext)) {
		 		 return true;
		 	} else {
		 		return false;
		 	}
		 }
		};
		
		String[] fileNames = pathNames.list(filter);
					 
		for (int i = 0; i < fileNames.length; i++) {
			File file = new File(pathNames.getAbsolutePath(), fileNames[i]);
		    
			if (fileNames[i].substring(0, 3).equals("BAB")) {
				infoData.setId("03");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Balbina");
				infoData.setLatitudeOfStation(-1.9311);
				infoData.setLongitudeOfStation(-59.4197);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("BRB")) {
				infoData.setId("10");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Brasília");
				infoData.setLatitudeOfStation(-15.6008);
				infoData.setLongitudeOfStation(-47.7131);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));												
			} else if (fileNames[i].substring(0, 3).equals("CAI")) {
				infoData.setId("20");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Caicó");
				infoData.setLatitudeOfStation(-6.4669);
				infoData.setLongitudeOfStation(-37.0847);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("CGR")) {
				infoData.setId("12");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Campo Grande");
				infoData.setLatitudeOfStation(-20.4383);
				infoData.setLongitudeOfStation(-54.5383);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("CHP")) {
				infoData.setId("06");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Chapecó");
				infoData.setLatitudeOfStation(-27.0800);
				infoData.setLongitudeOfStation(-52.6144);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("CBA")) {
				infoData.setId("21");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Cuiabá");
				infoData.setLatitudeOfStation(-15.5553);
				infoData.setLongitudeOfStation(-56.0700);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("FLN")) {
				infoData.setId("01");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Florianópolis");
				infoData.setLatitudeOfStation(-27.6017);
				infoData.setLongitudeOfStation(-48.5178);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));								
			} else if (fileNames[i].substring(0, 3).equals("JOI")) {
				infoData.setId("04");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Joinville");
				infoData.setLatitudeOfStation(-26.2525);
				infoData.setLongitudeOfStation(-48.8578);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));								
			} else if (fileNames[i].substring(0, 3).equals("LEB")) {
				infoData.setId("02");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Lebon Régis");
				infoData.setLatitudeOfStation(-26.9886);
				infoData.setLongitudeOfStation(-50.7150);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));								
			} else if (fileNames[i].substring(0, 3).equals("MCL")) {
				infoData.setId("45");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Montes Claros");
				infoData.setLatitudeOfStation(-16.6864);
				infoData.setLongitudeOfStation(-43.8688);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("NAT")) {
				infoData.setId("17");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Natal");
				infoData.setLatitudeOfStation(-5.8367);
				infoData.setLongitudeOfStation(-35.2064);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));	
			} else if (fileNames[i].substring(0, 3).equals("ORN")) {
				infoData.setId("28");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Ourinhos");
				infoData.setLatitudeOfStation(-22.9486);
				infoData.setLongitudeOfStation(-49.8942);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("PMA")) {
				infoData.setId("19");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Palmas");
				infoData.setLatitudeOfStation(-10.1778);
				infoData.setLongitudeOfStation(-48.3619);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));							
			} else if (fileNames[i].substring(0, 3).equals("PTR")) {
				infoData.setId("11");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Petrolina");
				infoData.setLatitudeOfStation(-9.0689);
				infoData.setLongitudeOfStation(-40.3197);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("RLM")) {
				infoData.setId("27");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Rolim de Moura");
				infoData.setLatitudeOfStation(-11.5817);
				infoData.setLongitudeOfStation(-61.7736);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));			
			} else if (fileNames[i].substring(0, 3).equals("SLZ")) {
				infoData.setId("16");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("São Luiz");
				infoData.setLatitudeOfStation(-2.5933);
				infoData.setLongitudeOfStation(-44.2122);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("SMS")) {
				infoData.setId("08");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("São Martinho da Serra");
				infoData.setLatitudeOfStation(-29.4428);
				infoData.setLongitudeOfStation(-53.8231);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("SBR")) {
				infoData.setId("05");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Sombrio");
				infoData.setLatitudeOfStation(-29.0956);
				infoData.setLongitudeOfStation(-49.8133);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("TMA")) {
				infoData.setId("44");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Termoaçu");
				infoData.setLatitudeOfStation(-5.3829);
				infoData.setLongitudeOfStation(-36.8191);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			} else if (fileNames[i].substring(0, 3).equals("UBE")) {
				infoData.setId("46");
				infoData.setInputData(file.getAbsolutePath());
				infoData.setOutputData(fileNames[i].substring(0, 7) + "ED.csv");
				infoData.setOutputCode(fileNames[i].substring(0, 7) + "ED_DQC.csv");
				infoData.setOutputReport(fileNames[i].substring(0, 7) + ".csv");
				infoData.setStation("Uberaba");
				infoData.setLatitudeOfStation(-19.998);
				infoData.setLongitudeOfStation(-47.900);
				infoData.setMonth(fileNames[i].substring(5, 7));
				infoData.setYear("20" + fileNames[i].substring(3, 5));
			}
			
			ScreeningController controller = new ScreeningController(file.getAbsolutePath());
			
			try {
			 Loader loader = new Loader();
			 loader.buildsMatrixData(file.getAbsolutePath());
			 loader.code = controller.validate(infoData.getLatitudeOfStation(), infoData.getLongitudeOfStation(), Integer.parseInt(infoData.getId()), Integer.parseInt(infoData.getMonth()));
			 loader.writeData(infoData.getOutputData(), loader.data, infoData.getId());
			 loader.writeCode(infoData.getOutputCode(), loader.data, loader.code, infoData.getId());
			 loader.writeReportData(infoData.getOutputReport(),infoData.getStation(), Integer.parseInt(infoData.getYear()), Integer.parseInt(infoData.getMonth()), infoData.getId(), loader.code, infoData.getLatitudeOfStation(), infoData.getLongitudeOfStation());
			 cont_std = controller.cont_std;
			} catch (LoaderException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Aviso:", 0);
				System.exit(1);
			}			
		}
		
		if (cont_std > 0) {
			JOptionPane.showMessageDialog(null, "Validação Concluída com Sucesso!!!\n\n"
					+ "Obs: Existem dados com desvio padrão 0 ", "Aviso:",JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Validação Concluída com Sucesso!!!","Aviso:",JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private static String ext;
	private static long cont_std = 0;
}
