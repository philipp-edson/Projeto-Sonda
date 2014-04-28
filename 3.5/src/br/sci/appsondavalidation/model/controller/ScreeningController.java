package br.sci.appsondavalidation.model.controller;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.acos;
import static java.lang.Math.pow;
//import static java.io.File.separator;

import java.io.File;

import br.sci.appsondavalidation.model.dataloader.Loader;

/**
 * 
 * @version 1.0.0
 * @author Rafael Carvalho Chagas
 * @since Copyright 2012-2013
 *
 */

public class ScreeningController {
	// Constant values used to get solar geometry data
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
    
    private final double e1 = 1.000110;
    private final double e2 = 0.034221;
    private final double e3 = 0.001280;
    private final double e4 = 0.000719;
    private final double e5 = 0.000077;
    
    // Threshold values used to qualify solar and meteorological data  - level 1
    private final int HUMI_MX = 100;
    private final int HUMI_MI = 0;
    
    private double PRES_MX;
    private double PRES_MI;

    private double TEMP_MX;
    private double TEMP_MI;
       
    private double PREC_MX;
    private final int PREC_MI = 0;
    
    private final int WS10_MX = 25;
    private final int WS10_MI = 0;
    
    private final int WD10_MX = 360;
    private final int WD10_MI = 0;
    
    /* BSRN criteria for solar data qualification */            
    private int LWDN_MX = 700;
    private int LWDN_MI = 40;

    private double GLOBAL_MX;
    private int GLOBAL_MI = -4;
    
    private double DIFUSE_MX;
    private int DIFUSE_MI = -4;
    
    private double DIRECT_MX;
    private int DIRECT_MI = -4;
    
    private double PAR_MX;
    private int PAR_MI = -4;
    
    private double LUX_MX;
    private int LUX_MI = -4;
    
    // Variables used to validate meteorological data  - levels 2 and  3
    private double temp_max = 0;
    private double temp_min = 999;
    private final int temp1h  = 59;
    private final int temp12h = 719;
    private double variation_temp1h;
    private double variation_temp12h;
    
    private double pres_max = 0;
    private double pres_min = 999;
    private final int pres3h  = 179;
    private double variation_pres3h;
    
    private double prec_max = 0;
    private double prec_min = 999;
    private final int prec1h  = 59;
    private final int prec24h = 1439;
    private double variation_prec1h;
    private double variation_prec24h;
    
    private double ws10_max = 0;
    private double ws10_min = 999;
    private final int ws103h  = 179;
    private final int ws1012h = 719;        
    private double variation_ws103h;
    private double variation_ws1012h;
    
    private double wd10_max = 0;
    private double wd10_min = 999;
    private final int wd103h  = 179;
    private final int wd1018h = 1079;
    private double variation_wd103h;
    private double variation_wd1018h;
    
    // Variables used to get solar geometry data
    private double e0;
    private double u0;              // Cosine of solar zenith angle
    private double zenith_angle;    // Zenith angle
    private double rtoa;            // Solar Irradiation at the top of atmosphere
    private double sa;
    private double day_angle;       // Diary angle
    private double dec;             // Declination angle
    private double eqtime;          // Equation time
    private double tcorr;           // Time correction
    private double hour_angle;      // Hour angle
    
    // Other variables
    private final double CDR = Math.PI / 180;
    private int rows;           // total file lines 
    private double num;         // Measurement time in minutes
    private double dia_jul;     // Day number
    private double horacorr;    // Time correction considering longitude data for the measurement site
    private double div;         // Measurement time in decimal hours
    public int cont_std = 0;
    
    // Variables used to count meteorological data valid - level 2 and 3 
    private int contTempValid = 0;
    private int contPresValid = 0;
    private int contPrecValid = 0;
    private int contWspdValid = 0;
    private int contWdirValid = 0;
    
    // Variables used to save the last valid meteorological data - level 2 and 3    
    private int lastTempValid;
    private int lastPresValid;
    private int lastPrecValid;
    private int lastWs10Valid;
    private int lastWd10Valid;
    
    private double kt;
    private double kn;
    
    private Loader loader;
    
    public ScreeningController() {
    	super();
    }
    
    public ScreeningController(String input) {
    	loader = new Loader();
    	loader.buildsMatrixData(input);
    	loader.buildsMatrixCode(input);
    }
    
    public int[][] validate(double latitude, double longitude, int station, int month) {
    	rows = loader.getRows() - 1;
    	    	
    	for (int i= 0; i<= rows; i++) {
    	    num = loader.data[i][3];
            div = num / 60;                             // Measurement time in utc time
            dia_jul = (int) loader.data[i][2];
            
            // Calculating astronomical data
            day_angle = (2 * PI / 365.25 * dia_jul);
            dec = (d0 - dc1 * cos(day_angle) + ds1 * sin(day_angle) - dc2 * cos(2 * day_angle) + ds2 * sin(2 * day_angle) - dc3 * cos(3 * day_angle) + ds3 * sin(3 * day_angle));
            eqtime = (et0 + tc1 * cos(day_angle) - ts1 * sin(day_angle) - tc2 * cos(2 * day_angle) - ts2 * sin(2 * day_angle)) * 229.18;
            tcorr = (eqtime + 4 * (longitude - 0 )) / 60;
            horacorr = tcorr + div;                     // Local time obtained from utc time
            hour_angle = (12.00 - horacorr) * 15;
            e0 = e1 + e2 * cos(day_angle) + e3 * sin(day_angle) + e4 * cos(2 * day_angle) + e5 * sin(2 * day_angle);
            u0 = sin(dec) * sin(latitude * CDR) + cos(dec) * cos(latitude * CDR) * cos(hour_angle * CDR);
            zenith_angle = (acos(u0)) * 180/Math.PI;
            sa = 1368 * e0;
                        
            // Start level 1
            
            // Start the routine to check the misalignment of the tracker
            if (zenith_angle < 87) {
            	if ((loader.data[i][4] != 3333) && (loader.data[i][4] != -5555) && (loader.data[i][4] != -6999)) {
            		if ((loader.data[i][28] != 3333) && (loader.data[i][28] != -5555) && (loader.data[i][28] != -6999)) {
            			if (loader.data[i][4] > 50) {
            				rtoa = sa * u0;
            				
            				kt = (loader.data[i][4] / rtoa);
            				kn = (loader.data[i][28] / loader.data[i][4]);
            				
            				if (kt >= 0.50) {
            					if (kn > 0.30) {
            						loader.code[i][8]  = 9;
            						loader.code[i][28] = 9;
            					} else {
            						loader.code[i][8]  = 552;
            						loader.code[i][28] = 552;
            					}
            				} else if ((kt >= 0.40) && (kt < 0.50)) {
            					if (kn > 0.10) {
            						loader.code[i][8]  = 9;
            						loader.code[i][28] = 9;
            					} else {
            						loader.code[i][8]  = 552;
            						loader.code[i][28] = 552;
            					}
            				}
            			}
            		}
            	}
            }   
            
            // End of routine to check the misalignment of the tracker 
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Global Radiation (W/m²) level 1
            if (loader.data[i][4] != 3333) {
            	if (loader.data[i][4] != -5555) {
            		if (loader.data[i][4] != -6999) {
            			if (loader.data[i][5] != 0) {
            				if (u0 > 0) {
                				GLOBAL_MX = (sa * 1.5 * pow(u0, 1.2) + 100);
                			} else {
                				GLOBAL_MX = 100;
                			}
                			
                			if ((loader.data[i][4] > GLOBAL_MI) && (loader.data[i][4] < GLOBAL_MX)) {
                				loader.code[i][4] = 9;
                			} else {
                				loader.code[i][4] = 552;
                			}
            			} else {
            				if (zenith_angle > 90) {
            					if (u0 > 0) {
            						GLOBAL_MX = (sa * 1.5 * pow(u0, 1.2) + 100);
            					} else {
            						GLOBAL_MX = 100;
            					}
            					
            					if ((loader.data[i][4] > GLOBAL_MI) && (loader.data[i][4] < GLOBAL_MX)) {
            						loader.code[i][4] = 9;
            					} else {
            						loader.code[i][4] = 552;
            					}
            				} else {
            					cont_std++;
            					
            					if ((loader.data[i][4] != loader.data[i - 1][4]) && (loader.data[i][4] != loader.data[i + 1][4])) {
            						if (u0 > 0) {
            							GLOBAL_MX = (sa * 1.5 * pow(u0, 1.2) + 100);
            						} else {
            							GLOBAL_MX = 100;
            						}
            						
            						if ((loader.data[i][4] > GLOBAL_MI) && (loader.data[i][4] < GLOBAL_MX)) {
            							loader.code[i][4] = 9;
            						} else {
            							loader.code[i][4] = 552;
            						}
            					} else {
            						loader.code[i][4] = 552;
            					}
            				}
            			}
            		} else {
            			loader.code[i][4] = -6999;
            		}
            	} else {
            		loader.code[i][4] = -5555;
            	}
            } else {
            	loader.code[i][4] = 3333;
            }
            
            // End of the routine validation: Global Radiation (W/m²) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Diffuse Radiation (W/m²) level 1
            if (loader.data[i][8] != 3333) {
            	if (loader.data[i][8] != -5555) {
            		if (loader.data[i][8] != -6999) {
            			if (loader.code[i][8] != 552) {
                			if (loader.data[i][9] != 0) {
                				if (u0 > 0) {
                    				DIFUSE_MX = (sa * 0.95 * pow(u0, 1.2) + 50);
                    			} else {
                    				DIFUSE_MX =  50;
                    			}
                				
                				if ((loader.data[i][8] > DIFUSE_MI) && (loader.data[i][8] < DIFUSE_MX)) {
                    				loader.code[i][8] = 9;
                    			} else {
                    				loader.code[i][8] = 552;
                    			}
                			} else {
                				if (zenith_angle > 90) {
                					if (u0 > 0) {
                						DIFUSE_MX = (sa * 0.95 * pow(u0, 1.2) + 50);
                					} else {
                						DIFUSE_MX = 50;
                					}
                					
                					if ((loader.data[i][8] > DIFUSE_MI) && (loader.data[i][8] < DIFUSE_MX)) {
                						loader.code[i][8] = 9;
                					} else {
                						loader.code[i][8] = 552;
                					}
                				} else {
                					cont_std++;
                					
                					if ((loader.data[i][8] != loader.data[i - 1][8]) && (loader.data[i][8] != loader.data[i + 1][8])) {
                						if (u0 > 0) {
                							DIFUSE_MX = (sa * 0.95 * pow(u0, 1.2) + 50);
                						} else {
                							DIFUSE_MX = 50;
                						}
                						
                						if ((loader.data[i][8] > DIFUSE_MI) && (loader.data[i][8] < DIFUSE_MX)) {
                							loader.code[i][8] = 9;
                						} else {
                							loader.code[i][8] = 552;
                						}
                					} else {
                						loader.code[i][8] = 552;
                					}
                				}
                			}
            			}
            		} else {
            			loader.code[i][8] = -6999;
            		}            			
            	} else {
            		loader.code[i][8] = -5555;
            	}
            } else {
            	loader.code[i][8] = 3333;
            }
            
            // End of the routine validation: Diffuse Radiation (W/m²) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Par Radiation (�mols s� m�) level 1
            if (loader.data[i][12] != 3333) {
            	if (loader.data[i][12] != -5555) {
            		if (loader.data[i][12] != -6999) {
            			if (loader.data[i][13] != 0) {
            				if (u0 > 0) {
                    			PAR_MX = 2.07 * (sa * 1.5 * pow(u0, 1.2) + 100);
                    		} else {
                    			PAR_MX = 2.07 * 100;
                    		}
                			
                			if ((loader.data[i][12] > PAR_MI) && (loader.data[i][12] < PAR_MX)) {
                    			loader.code[i][12] = 9;
                    		} else {
                    			loader.code[i][12] = 552;
                    		}
            			} else {
            				if (zenith_angle > 90) {
            					if (u0 > 0) {
            						PAR_MX = 2.07 * (sa * 1.5 * pow(u0, 1.2) + 100);
            					} else {
            						PAR_MX = 2.07 * 100;
            					}
            					
            					if ((loader.data[i][12] > PAR_MI) && (loader.data[i][12] < PAR_MX)) {
            						loader.code[i][12] = 9;
            					} else {
            						loader.code[i][12] = 552;
            					}
            				} else {
            					cont_std++;
            					
            					if ((loader.data[i][12] != loader.data[i - 1][12]) && (loader.data[i][12] != loader.data[i + 1][12])) {
            						if (u0 > 0) {
            							PAR_MX = 2.07 * (sa * 1.5 * pow(u0, 1.2) + 100);
            						} else {
            							PAR_MX = 2.07 * 100;
            						}
            						
            						if ((loader.data[i][12] > PAR_MI) && (loader.data[i][12] < PAR_MX)) {
            							loader.code[i][12] = 9;
            						} else {
            							loader.code[i][12] = 552;
            						}
            					} else {
            						loader.code[i][12] = 552;
            					}
            				}
            			}
            		} else {
            			loader.code[i][12] = -6999;
            		}
            	} else {
            		loader.code[i][12] = -5555;
            	}
            } else {
            	loader.code[i][12] = 3333;
            }
            
            // End of the routine validation: Par Radiation (�mols s� m�) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Lux Radiation (kLux) level 1
            if (loader.data[i][16] != 3333) {
            	if (loader.data[i][16] != -5555) {
            		if (loader.data[i][16] != -6999) {
            			if (loader.data[i][17] != 0) {
            				if (u0 > 0) {
                    			LUX_MX = 0.115 * (sa * 1.5 * pow(u0, 1.2) + 100);
                    		} else {
                    			LUX_MX = 0.115 * 100;
                    		}
                			
                			if ((loader.data[i][16] > LUX_MI) && (loader.data[i][16] < LUX_MX)) {
                    			loader.code[i][16] = 9;
                    		} else {
                    			loader.code[i][16] = 552;
                    		}            				
            			} else {
            				if (zenith_angle > 90) {
            					if (u0 > 0) {
            						LUX_MX = 0.115 * (sa * 1.5 * pow(u0, 1.2) + 100);
            					} else {
            						LUX_MX = 0.115 * 100;
            					}
            					
            					if ((loader.data[i][16] > LUX_MI) && (loader.data[i][16] < LUX_MX)) {
            						loader.code[i][16] = 9;
            					} else {
            						loader.code[i][16] = 552;
            					}
            				} else {
            					cont_std++;
            					
            					if ((loader.data[i][16] != loader.data[i - 1][16]) && (loader.data[i][16] != loader.data[i + 1][16])) {
            						if (u0 > 0) {
            							LUX_MX = 0.115 * (sa * 1.5 * pow(u0, 1.2) + 100);
            						} else {
            							LUX_MX = 0.115 * 100;
            						}
            						
            						if ((loader.data[i][16] > LUX_MI) && (loader.data[i][16] < LUX_MX)) {
            							loader.code[i][16] = 9;
            						} else {
            							loader.code[i][16] = 552;
            						}
            					} else {
            						loader.code[i][16] = 552;
            					}
            				}
            			}
            		} else {
            			loader.code[i][16] = -6999;
            		}
            	} else {
            		loader.code[i][16] = -5555;
            	}
            } else {
            	loader.code[i][16] = 3333;
            }
            
            // End of the routine validation: Lux Radiation (kLux) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Air Temperature (°C) level 1
            if (loader.data[i][20] != 3333) {
            	if (loader.data[i][20] != -5555) {
            		TEMP_MX = loader.getTempMax("." + File.separator + "limits" + File.separator + "temp.max", station, month);
            		TEMP_MI = loader.getTempMin("." + File.separator + "limits" + File.separator + "temp.min", station, month);
            		
            		if ((loader.data[i][20] > TEMP_MI) && (loader.data[i][20] < TEMP_MX)) {
            			loader.code[i][20] = 9;
            		} else {
            			loader.code[i][20] = 552;
            		}
            	} else {
            		loader.code[i][20] = -5555;
            	}
            } else {
            	loader.code[i][20] = 3333;
            }
            
            // End of the routine validation: Air Temperature (°C) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Relative Air Humidity (%) level 1
            if (loader.data[i][21] != 3333) {
            	if (loader.data[i][21] != -5555) {
            		if ((loader.data[i][21] > HUMI_MI) && (loader.data[i][21] <= HUMI_MX)) {
            			loader.code[i][21] = 9;
            		} else {
            			loader.code[i][21] = 552;
            		}
            	} else {
            		loader.code[i][21] = -5555;
            	}
            } else {
            	loader.code[i][21] = 3333;
            }
            
            // End of the routine validation: Relative Air Humidity (%) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Atmospheric Pressure (mbar) level 1
            if (loader.data[i][22] != 3333) {
            	if (loader.data[i][22] != -5555) {            		
            		PRES_MX = loader.getPresMax("." + File.separator + "limits" + File.separator + "pres.max", station);
            		PRES_MI = loader.getPresMin("." + File.separator + "limits" + File.separator + "pres.min", station);
            		
            		if ((loader.data[i][22] > PRES_MI) && (loader.data[i][22] < PRES_MX)) {
            			loader.code[i][22] = 9;
            		} else {
            			loader.code[i][22] = 552;
            		}
            	} else {
            		loader.code[i][22] = -5555;
            	}
            } else {
            	loader.code[i][22] = 3333;
            }
            
            // End of the routine validation: Atmospheric Pressure (mbar) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Accumulated Precipitation (mm) level 1
            if (loader.data[i][23] != 3333) {
            	if (loader.data[i][23] != -5555) {
            		PREC_MX = loader.getPrecMax("." + File.separator + "limits" + File.separator + "prec.max", station, month);
            		
            		if ((loader.data[i][23] >= PREC_MI) && (loader.data[i][23] < PREC_MX)) {
            			loader.code[i][23] = 9;
            		} else {
            			loader.code[i][23] = 552;
            		}
            	} else {
            		loader.code[i][23] = -5555;
            	}
            } else {
            	loader.code[i][23] = 3333;
            }
            
            // End of the routine validation: Accumulated Precipitation (mm) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Wind Speed 10m (m/s) level 1
            if (loader.data[i][24] != 3333) {
            	if (loader.data[i][24] != -5555) {
            		if ((loader.data[i][24] > WS10_MI) && (loader.data[i][24] < WS10_MX)) {
            			loader.code[i][24] = 9;
            		} else {
            			loader.code[i][24] = 552;
            		}
            	} else {
            		loader.code[i][24] = -5555;
            	}
            } else {
            	loader.code[i][24] = 3333;
            }
            
            // End of the routine validation: Wind Speed 10m (m/s) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Wind Direction 10m (°) level 1
            if (loader.data[i][25] != 3333) {
            	if (loader.data[i][25] != -5555) {
            		if (loader.data[i][26] != 0) {
            			if ((loader.data[i][25] > WD10_MI) && (loader.data[i][25] < WD10_MX)) {
                			loader.code[i][25] = 9;
                		} else {
                			loader.code[i][25] = 552;
                		}
            		} else {
            			loader.code[i][25] = 552;
            		}
            	} else {
            		loader.code[i][25] = -5555;
            	}
            } else {
            	loader.code[i][25] = 3333;
            }
            
            // End of the routine validation: Wind Direction 10m (°) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Direct Radiation (W/m²) level 1
            if (loader.data[i][28] != 3333) {
            	if (loader.data[i][28] != -5555) {
            		if (loader.data[i][28] != -6999) {
            			if (loader.code[i][28] != 552) {
            				if (loader.data[i][29] != 0) {
                				if (u0 > 0) {
                  		        	DIRECT_MX = sa;
                		        } else {
                 			        DIRECT_MX = 50;
                		        }
                				
                				if ((loader.data[i][28] > DIRECT_MI) && (loader.data[i][28] < DIRECT_MX)) {
                    				loader.code[i][28] = 9;
                    			} else {
                    				loader.code[i][28] = 552;
                    			}
                			} else {
                				if (zenith_angle > 90) {
                					if (u0 > 0) {
                						DIRECT_MX = sa;
                					} else {
                						DIRECT_MX = 50;
                					}
                					
                					if ((loader.data[i][28] > DIRECT_MI) && (loader.data[i][28] < DIRECT_MX)) {
                						loader.code[i][28] = 9;
                					} else {
                						loader.code[i][28] = 552;
                					}
                				} else {
                					cont_std++;
                					
                					if ((loader.data[i][28] != loader.data[i - 1][28]) && (loader.data[i][28] != loader.data[i + 1][28])) {
                						if (u0 > 0) {
                							DIRECT_MX = sa;
                						} else {
                							DIRECT_MX = 50;
                						}
                						
                						if ((loader.data[i][28] > DIRECT_MI) && (loader.data[i][28] < DIRECT_MX)) {
                							loader.code[i][28] = 9;
                						} else {
                							loader.code[i][28] = 552;
                						}
                					} else {
                						loader.code[i][28] = 552;
                					}
                				}
                			}
            			}
            		} else {
            			loader.code[i][28] = -6999;
            		}
            	} else {
            		loader.code[i][28] = -5555;
            	}
            } else {
            	loader.code[i][28] = 3333;
            }
            
            // End of the routine validation: Direct Radiation (W/m²) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Long Wave Radiation (W/m²) level 1
            if (loader.data[i][32] != 3333) {
            	if (loader.data[i][32] != -5555) {
            		if (loader.data[i][32] != -6999) {
            			if (loader.data[i][33] != 0) {
            				if ((loader.data[i][32] > LWDN_MI) && (loader.data[i][32] < LWDN_MX)) {
            					loader.code[i][32] = 9;
                    		} else {
                    			loader.code[i][32] = 552;
                    		}
                		} else {
                			if (zenith_angle > 90) {
                				if ((loader.data[i][32] > LWDN_MI) && (loader.data[i][32] < LWDN_MX)) {
                					loader.code[i][32] = 9;
                				} else {
                					loader.code[i][32] = 552;
                				}
                			} else {
                				cont_std++;
                					
                				if ((loader.data[i][32] != loader.data[i - 1][32]) && (loader.data[i][32] != loader.data[i + 1][32])) {
                					if ((loader.data[i][32] > LWDN_MI) && (loader.data[i][32] < LWDN_MX)) {
                						loader.code[i][32] = 9;
                					} else {
                						loader.code[i][32] = 552;
                					}
                				} else {
                					loader.code[i][32] = 552;
                				}
                			}
                		}
            		} else {
            			loader.code[i][32] = -6999;
            		}
            	} else {
            		loader.code[i][32] = -5555;
            	}
            } else {
            	loader.code[i][32] = 3333;
            }          
            
            // End of the routine validation: Long Wave Radiation (W/m²) level 1
            
            //----------------------------------------------------------------------------------------------------------------------
    	} // End of loop level 1
    	
    	// Start level 2
    	for (int i= 0; i<= rows; i++) {
    	    num = loader.data[i][3];
            div = num / 60;
            dia_jul = (int) loader.data[i][2];
            
            // Calculating astronomical data
            day_angle = (2 * PI / 365.25 * dia_jul);
            dec = (d0 - dc1 * cos(day_angle) + ds1 * sin(day_angle) - dc2 * cos(2* day_angle) + ds2 * sin(2* day_angle) - dc3 * cos(3* day_angle) + ds3 * sin(3* day_angle));
            eqtime = (et0 + tc1 * cos(day_angle) - ts1 * sin(day_angle) - tc2 * cos(2* day_angle) - ts2 * sin(2* day_angle)) * 229.18;
            tcorr = (eqtime + 4 * (longitude - 0 )) / 60;
            horacorr = tcorr + div;                     // Measurement time in utc
            hour_angle = (12.00 - horacorr) * 15;
            e0 = e1 + e2 * cos(day_angle) + e3 * sin(day_angle) + e4 * cos(2 * day_angle) + e5 * sin(2 * day_angle);
            u0 = sin(dec) * sin(latitude * CDR) + cos(dec) * cos(latitude * CDR) * cos(hour_angle * CDR);
            zenith_angle = (acos(u0)) * 180/Math.PI;
            sa = 1368 * e0;
            
            // BSRN criteria used to qualify solar data as RARE events
            GLOBAL_MI = -2;
            DIFUSE_MI = -2;
            DIRECT_MI = -2;
            PAR_MI = -2;
            LUX_MI = -2;
            LWDN_MX = 500;
            LWDN_MI = 60;
            
            // Variables used to validate meteorological data - level 2
            final int totalTemp1h_1 = rows - temp1h; 
            final int totalTemp1h_2 = rows - temp1h + 1;
            
            final int totalPres3h_1 = rows - pres3h;
            final int totalPres3h_2 = rows - pres3h + 1;
            
            final int totalPrec1h_1 = rows - prec1h;
            final int totalPrec1h_2 = rows - prec1h + 1;
            
            final int totalWs103h_1 = rows - ws103h;
            final int totalWs103h_2 = rows - ws103h;
            
            final int totalWd103h_1 = rows - wd103h;
            final int totalWd103h_2 = rows - wd103h + 1;
                        
            // Start of the routine validation: Global Radiation (W/m²) level 2
            if ((loader.code[i][4] != 3333) && (loader.code[i][4] != -5555) && (loader.code[i][4] != -6999) && (loader.code[i][4] != 552)) {
            	if (u0 > 0) {
            		GLOBAL_MX = (sa * 1.2 * pow(u0, 1.2) + 50);
            	} else {
            		GLOBAL_MX =  50;
            	}
            	
            	if ((loader.data[i][4] > GLOBAL_MI) && (loader.data[i][4] < GLOBAL_MX)) {
            		loader.code[i][4] = 999;
            	} else {
            		loader.code[i][4] = 29;
            	}
            }
            
            // End of the routine validation: Global Radiation (W/m²) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Diffuse Radiation (W/m²) level 2
            if ((loader.code[i][8] != 3333) && (loader.code[i][8] != -5555) && (loader.code[i][8] != -6999) && (loader.code[i][8] != 552)) {
            	if (u0 > 0) {
            		DIFUSE_MX = (sa * 0.75 * pow(u0, 1.2) + 30);
            	} else {
            		DIFUSE_MX =  30;
            	}
            	
            	if ((loader.data[i][8] > DIFUSE_MI) && (loader.data[i][8] < DIFUSE_MX)) {
            		loader.code[i][8] = 99;
            	} else {
            		loader.code[i][8] = 29;
            	}
            }
            
            // End of the routine validation: Diffuse Radiation (W/m²) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Par Radiation (�mols s� m�) level 2
            if ((loader.code[i][12] != 3333) && (loader.code[i][12] != -5555) && (loader.code[i][12] != -6999) && (loader.code[i][12] != 552)) {
            	if (u0 > 0) {
            		PAR_MX = 2.07 * (sa * 1.2 * pow(u0, 1.2) + 50);
            	} else {
            		PAR_MX = 2.07 * 50;
            	}
            	
            	if ((loader.data[i][12] > PAR_MI) && (loader.data[i][12] < PAR_MX)) {
            		loader.code[i][12] = 99;
            	} else {
            		loader.code[i][12] = 29;
            	}
            }
            
            // End of the routine validation: Par Radiation (�mols s� m�) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Lux Radiation (kLux) level 2
            if ((loader.code[i][16] != 3333) && (loader.code[i][16] != -5555) && (loader.code[i][16] != -6999) && (loader.code[i][16] != 552)) {
            	if (u0 > 0) {
            		LUX_MX = 0.115 * (sa * 1.2 * pow(u0, 1.2) + 50);
            	} else {
            		LUX_MX = 0.115 * 50;
            	}
            	
            	if ((loader.data[i][16] > LUX_MI) && (loader.data[i][16] < LUX_MX)) {
            		loader.code[i][16] = 99;
            	} else {
            		loader.code[i][16] = 29;
            	}
            }
            
            // End of the routine validation: Lux Radiation (kLux) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Air Temperature (°C) level 2
            if ((loader.code[i][20] != 3333) && (loader.code[i][20] != -5555) && (loader.code[i][20] != 552)) {
            	if (i <= totalTemp1h_1) {
            		int j= 0;
            		while (j <= temp1h) {
            			if ((loader.code[i + j][20] != 3333) && (loader.code[i + j][20] != 552)) {
            				contTempValid++;
            				
            				if (loader.data[i + j][20] > temp_max) {
            					temp_max = loader.data[i + j][20];
            				}
            				
            				if (loader.data[i + j][20] < temp_min) {
            					temp_min = loader.data[i + j][20];
            				}
            				
            				variation_temp1h = temp_max - temp_min;
            			}
            			j++;
            		}
            		
            		if (contTempValid >= 40) {
            			if (variation_temp1h < 5) {
                			loader.code[i][20] = 99;
                			lastTempValid = loader.code[i][20];
                		} else {
                			loader.code[i][20] = 529;
                		}
            		} else {
            			int l= 0;
            			while (l <= temp1h) {
            				if ((loader.code[i + l][20] != 3333) && (loader.code[i + l][20] != 552)) {
            					if (i == 0) {
            						loader.code[i][20] = 559;		
            					} else {
            						loader.code[i][20] = lastTempValid;
            					}
            				}
            			   	l++;
            			}
            		}
            		contTempValid = 0;
            		temp_max = 0;
            		temp_min = 999;
            	}
            	
            	if (i >= totalTemp1h_2) {
            		loader.code[i][20] = loader.code[totalTemp1h_1][20];
            	}
            }
            
            // End of the routine validation: Air Temperature (°C) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Atmospheric Pressure (mbar) level 2
            if ((loader.code[i][22] != 3333) && (loader.code[i][22] != -5555) && (loader.code[i][22] != 552)) {
            	if (i <= totalPres3h_1) {
            		int j= 0;
            		while (j <= pres3h) {
            			if ((loader.code[i + j][22] != 3333) && (loader.code[i + j][22] != 552)) {
            				contPresValid++;
            				
            				if (loader.data[i + j][22] > pres_max) {
            					pres_max = loader.data[i + j][22];
            				}
            				
            				if (loader.data[i + j][22] < pres_min) {
            					pres_min = loader.data[i + j][22];
            				}
            				
            				variation_pres3h = pres_max - pres_min;
            			}
            			j++;
            		}
            		
            		if (contPresValid >= 40) {
            			if (variation_pres3h < 6) {
            				loader.code[i][22] = 99;
            				lastPresValid = loader.code[i][22];
            			} else {
            				loader.code[i][22] = 529;
            			}
            		} else {
            			int l= 0;
            			while (l <= pres3h) {
            				if ((loader.code[i + l][22] != 3333) && (loader.code[i + l][22] != 552)) {
            					if (i == 0) {
            						loader.code[i][22] = 59;
            					} else {
            						loader.code[i][22] = lastPresValid;
            					}
            				}
            				l++;
            			}
            		}
            		contPresValid = 0;
            		pres_max = 0;
            		pres_min = 999;
            	}
            	
            	if (i >= totalPres3h_2) {
            		loader.code[i][22] = loader.code[totalPres3h_1][22];
            	}
            }
            
            // End of the routine validation: Atmospheric Pressure (mbar) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Accumulated Precipitation (mm) level 2
            if ((loader.code[i][23] != 3333) && (loader.code[i][23] != -5555) && (loader.code[i][23] != 552)) {
            	if (i <= totalPrec1h_1) {
            		int j= 0;
            		while (j <= prec1h) {
            			if ((loader.code[i + j][23] != 3333) && (loader.code[i + j][23] != 552)) {
            				contPrecValid++;
            				
            				if (loader.data[i + j][23] > prec_max) {
            					prec_max = loader.data[i + j][23];
            				}
            				
            				if (loader.data[i + j][23] < prec_min) {
            					prec_min = loader.data[i + j][23];
            				}
            				
            				variation_prec1h = prec_max - prec_min; 
            			}
            			j++;
            		}
            		
            		if (contPrecValid >= 40) {
            			if (variation_prec1h < 25) {
            				loader.code[i][23] = 99;
            				lastPrecValid = loader.code[i][23];
            			} else {
            				loader.code[i][23] = 529;
            			}
            		} else {
            			int l= 0;
            			while (l <= prec1h) {
            				if ((loader.code[i + l][23] != 3333) && (loader.code[i + l][23] != 552)) {
            					if (i == 0) {
            						loader.code[i][23] = 559;
            					} else {
            						loader.code[i][23] = lastPrecValid;
            					}
            				}
            				l++;
            			}
            		}
            		contPrecValid = 0;
        			prec_max = 0;
        			prec_min = 999;
            	}
            	
            	if (i >= totalPrec1h_2) {
            		loader.code[i][23] = loader.code[totalPrec1h_1][23];
            	}
            }
            
            // End of the routine validation: Accumulated Precipitation (mm) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Wind Speed 10m (m/s) level 2
            if ((loader.code[i][24] != 3333) && (loader.code[i][24] != -5555) && (loader.code[i][24] != 552)) {
            	if (i <= totalWs103h_1) {
            		int j= 0;
            		while (j <= ws103h) {
            			if ((loader.code[i + j][24] != 3333) && (loader.code[i + j][24] != 552)) {
            				contWspdValid++;
            				
            				if (loader.data[i + j][24] > ws10_max) {
            					ws10_max = loader.data[i + j][24];
            				}
            				
            				if (loader.data[i + j][24] < ws10_min) {
            					ws10_min = loader.data[i + j][24];
            				}
            				
            				variation_ws103h = ws10_max - ws10_min;
            			}
            			j++;
            		}
            		
            		if (contWspdValid >= 40) {
            			if (variation_ws103h > 0.1) {
            				loader.code[i][24] = 99;
            				lastWs10Valid = loader.code[i][24];
            			} else {
            				loader.code[i][24] = 529;
            			}
            		} else {
            			int l= 0;
            			while (l <= ws103h) {
            				if ((loader.code[i + l][24] != 3333) && (loader.code[i + l][24] != 552)) {
            					if (i == 0) {
            						loader.code[i][24] = 559;
            					} else {
            						loader.code[i][24] = lastWs10Valid;
            					}
            				}
            				l++;
            			}
            		}
            		contWspdValid = 0;
            		ws10_max = 0;
            		ws10_min = 999;
            	}
            	
            	if (i >= totalWs103h_2) {
            		loader.code[i][24] = loader.code[totalWs103h_1][24];
            	}
            }
            
            // End of the routine validation: Wind Speed 10m (m/s) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Wind Direction 10m (°) level 2
            if ((loader.code[i][25] != 3333) && (loader.code[i][25] != -5555) && (loader.code[i][25] != 552)) {
            	if (i <= totalWd103h_1) {
            		int j= 0;
            		while (j <= wd103h) {
            			if ((loader.code[i + j][25] != 3333) && (loader.code[i + j][25] != 552)) {
            				contWdirValid++;
            				
            				if (loader.data[i + j][25] > wd10_max) {
            					wd10_max = loader.data[i + j][25];
            				}
            				
            				if (loader.data[i + j][25] < wd10_min) {
            					wd10_min = loader.data[i + j][25];
            				}
            				
            				variation_wd103h = wd10_max - wd10_min;
            			}
            			j++;
            		}
            		
            		if (contWdirValid >= 40) {
            			if (variation_wd103h > 1) {
            				loader.code[i][25] = 99;
            				lastWd10Valid = loader.code[i][25];
            			} else
            				loader.code[i][25] = 529;
            		} else {
            			int l= 0;
            			while (l <= wd103h) {
            				if ((loader.code[i + l][25] != 3333) && (loader.code[i + l][25] != 552)) {
            					if (i == 0) {
            						loader.code[i][25] = 559;
            					} else {
            						loader.code[i][25] = lastWd10Valid;
            					}
            				}
            				l++;
            			}
            		}
            		contWdirValid = 0;
            		wd10_max = 0;
            		wd10_min = 999;
            	}
            	
            	if (i >= totalWd103h_2) {
            		loader.code[i][25] = loader.code[totalWd103h_1][25];
            	}
            }
            
            // End of the routine validation: Wind Direction 10m (°) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Direct Radiation (W/m²) level 2
            if ((loader.code[i][28] != 3333) && (loader.code[i][28] != -5555) && (loader.code[i][28] != -6999) && (loader.code[i][28] != 552)) {
            	if (u0 > 0) {
            		DIRECT_MX = (sa * 0.95 * Math.pow(u0, 0.2) + 10);
            	} else {
            		DIRECT_MX =  10;
            	}
            	
            	if ((loader.data[i][28] > DIRECT_MI) && (loader.data[i][28] < DIRECT_MX)) {
            		loader.code[i][28] = 99;
            	} else {
            		loader.code[i][28] = 29;
            	}
            }
            
            // End of the routine validation: Direct Radiation (W/m²) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
            
            // Start of the routine validation: Long Wave Radiation (W/m²) level 2
            if ((loader.code[i][32] != 3333) && (loader.code[i][32] != -5555)  && (loader.code[i][32] != -6999) && (loader.code[i][32] != 552)) {
            	if ((loader.data[i][32] > LWDN_MI) && (loader.data[i][32] < LWDN_MX)) {
            		loader.code[i][32] = 99;
            	} else {
            		loader.code[i][32] = 29;
            	}
            }
            
            // End of the routine validation: Long Wave Radiation (W/m²) level 2
            
            //----------------------------------------------------------------------------------------------------------------------
    	} // End of loop level 2
    	
    	// Start level 3
    	for (int i= 0; i<= rows; i++) {
    		num = loader.data[i][3];
            div = num / 60;
            dia_jul = (int) loader.data[i][2];
            
            // Calculating astronomical geometry data
            day_angle = (2 * PI / 365.25 * dia_jul);
            dec = (d0 - dc1 * cos(day_angle) + ds1 * sin(day_angle) - dc2 * cos(2* day_angle) + ds2 * sin(2* day_angle) - dc3 * cos(3* day_angle) + ds3 * sin(3* day_angle));
            eqtime = (et0 + tc1 * cos(day_angle) - ts1 * sin(day_angle) - tc2 * cos(2* day_angle) - ts2 * sin(2* day_angle)) * 229.18;
            tcorr = (eqtime + 4 * (longitude - 0 )) / 60;
            horacorr = tcorr + div;                   // Measurement time in utc
            hour_angle = (12.00 - horacorr) * 15;
            e0 = e1 + e2 * cos(day_angle) + e3 * sin(day_angle) + e4 * cos(2 * day_angle) + e5 * sin(2 * day_angle);
            u0 = sin(dec) * sin(latitude * CDR) + cos(dec) * cos(latitude * CDR) * cos(hour_angle * CDR);
            zenith_angle = (acos(u0)) * 180/Math.PI;
            sa = 1368 * e0;
            
            // Variables used to validate radiation Global and Difuse - level 3         
            double difSw = 0;
            double sumSw = 0;
            double divSw = 0;
            
            // Variables used to validate radiation Direct - level 3
            double direct_h, direct_n, direct_p;
            
            // Variables used to validate radiation long wave - level 3
            final double sigma = 5.67E-8;
            double temp, temp_a, temp_b;
            
            // Variables used in the comparison of global radiation with par and lux - level 3
            double lux_global, par_global, lux_par, par_lux;
			double[] mat_desvio = new double[4];
			int[] mat_limite = new int[4];
			int mat_lppl;
			
			// Variables used to validate meteorological data - level 3
			int totalTemp12h_1 = rows - temp12h;
            int totalTemp12h_2 = rows - temp12h + 1;
                        
            int totalPrec24h_1 = rows - prec24h;
            int totalPrec24h_2 = rows - prec24h + 1;
            
            int totalWs1012h_1 = rows - ws1012h;
            int totalWs1012h_2 = rows - ws1012h + 1;
            
            int totalWd1018h_1 = rows - wd1018h;
            int totalWd1018h_2 = rows - wd1018h + 1;
            
            // Start of the routine validation: Comparison of global radiation with par and lux level 3
         	if ((loader.code[i][4] != 3333) && (loader.code[i][4] != -5555) && (loader.code[i][4] != -6999) && (loader.code[i][4] != 552) && (loader.code[i][4] != 29)) {
         		if ((loader.code[i][12] != 3333) && (loader.code[i][12] != -5555) && (loader.code[i][12] != -6999) && (loader.code[i][12] != 552) && (loader.code[i][12] != 29)) {
         			if ((loader.code[i][16] != 3333) && (loader.code[i][16] != -5555) && (loader.code[i][16] != -6999) && (loader.code[i][16] != 552) && (loader.code[i][16] != 29)) {
         				if (zenith_angle < 90) {
         					lux_global = 0.115 * loader.data[i][4];
                         	par_global = 2.07 * loader.data[i][4];
                            // There are some dependency with photosynthetic photon flux and sometimes 18 is not a better number.
                         	// This version (3.5) these terms were recalculated by Prof. Enio. 
                         	lux_par    = loader.data[i][12] /(double)20.83;
                         	par_lux    = 20.83 * loader.data[i][16];  
                         	
                         	if(loader.data[i][12] == 0) {
                         		loader.data[i][12] = 0.001;
                         	}
                         	
                         	if(loader.data[i][16] == 0) {
                         		loader.data[i][16] = 0.001;
                         	}
                         	
                         	//  LUX - LUX<-GLO
                         	mat_desvio[0] = (Math.abs(loader.data[i][16] - lux_global) / loader.data[i][16]) * 100;
                         	//  LUX - LUX<-PAR
                         	mat_desvio[2] = (Math.abs(loader.data[i][16] - lux_par) / loader.data[i][16]) * 100;
                         	//  PAR - PAR<-GLO
                         	mat_desvio[1] = (Math.abs(loader.data[i][12] - par_global) / loader.data[i][12]) * 100;
                         	//  PAR - PAR<-LUX
                         	mat_desvio[3] = (Math.abs(loader.data[i][12] - par_lux) / loader.data[i][12]) * 100;
                         	
                         	if (zenith_angle < 80) {
                         		// LUX<-GLO
                         		if (mat_desvio[0] < 9.5)        // Padr�o 0
                         			mat_limite[0] = 0;
                         		else if (mat_desvio[0] < 33.5)  // Padr�o 1
                         			mat_limite[0] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[0] = 2;
                         				
                         		// PAR<-GLO
                         		if (mat_desvio[1] < 12)         // Padr�o 0
                         			mat_limite[1] = 0;
                         		else if (mat_desvio[1] < 34)    // Padr�o 1
                         			mat_limite[1] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[1] = 2;
                         				
                         		// LUX<-PAR
                         		if (mat_desvio[2] < 7.25)       // Padr�o 0
                         			mat_limite[2] = 0;
                         		else if (mat_desvio[2] < 15.5)  // Padr�o 1
                         			mat_limite[2] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[2] = 2;
                         				
                         		// PAR<-LUX
                         		if (mat_desvio[3] < 6.75)       // Padr�o 0
                         			mat_limite[3] = 0;
                         		else if (mat_desvio[3] < 13.5)  // Padr�o 1
                         			mat_limite[3] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[3] = 2;
                         	} else if (zenith_angle <= 88) {
                         		// LUX<-GLO
                         		if (mat_desvio[0] < 16.5)       // Padr�o 0
                         			mat_limite[0] = 0;
                         		else if (mat_desvio[1] < 65)    // Padr�o 1
                         			mat_limite[0] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[0] = 2;
                         				
                         		// PAR<-GLO
                         		if (mat_desvio[1] < 17)         // Padr�o 0
                         			mat_limite[1] = 0;
                         		else if (mat_desvio[1] < 66.5)  // Padr�o 1
                         			mat_limite[1] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[1] = 2;
                         				
                         		// LUX<-PAR
                         		if (mat_desvio[2] < 11.75)      // Padr�o 0
                         			mat_limite[2] = 0;
                         		else if (mat_desvio[2] < 23.5)  // Padr�o 1
                         			mat_limite[2] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[2] = 2;
                         				
                         		// PAR<-LUX
                         		if (mat_desvio[3] < 10.5)       // Padr�o 0
                         			mat_limite[3] = 0;
                         		else if (mat_desvio[3] < 19)    // Padr�o 1
                         			mat_limite[3] = 1;
                         		else 						    // Padr�o 2
                         			mat_limite[3] = 2;
                         	} 
                         			
                         	if ((zenith_angle > 88) && (zenith_angle <= 90)) {
                         		// LUX<-GLO
                         		if (mat_desvio[0] < 45.5)       // Padr�o 0
                         			mat_limite[0] = 0;
                         		else if (mat_desvio[0] < 100)   // Padr�o 1
                         			mat_limite[0] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[0] = 2;
                         				
                         		// PAR<-GLO
                         		if (mat_desvio[1] < 51.25)      // Padr�o 0
                         			mat_limite[1] = 0;
                         		else if (mat_desvio[1] < 99.25) // Padr�o 1
                         			mat_limite[1] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[1] = 2;
                         				
                         		// LUX<-PAR
                         		if (mat_desvio[2] < 17.75)      // Padr�o 0
                         			mat_limite[2] = 0;
                         		else if (mat_desvio[2] < 43.5)  // Padr�o 1
                         			mat_limite[2] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[2] = 2;
                         				
                         		// PAR<-LUX
                         		if (mat_desvio[3] < 15.5)       // Padr�o 0
                         			mat_limite[3] = 0;
                         		else if (mat_desvio[3] < 30.5)  // Padr�o 1
                         			mat_limite[3] = 1;
                         		else                            // Padr�o 2
                         			mat_limite[3] = 2;
                         	}
                         			
                         	if(mat_limite[2] >= mat_limite[3]) {
                         		mat_lppl = mat_limite[2];
                         	} else {
                         		mat_lppl = mat_limite[3];
                         	}
                         			
                         	if ((mat_limite[1] == 0) && (mat_lppl == 0) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 0) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 0) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 1) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 1) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 1) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 2) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 2) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 0) && (mat_lppl == 2) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 0) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 0) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 0) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 1) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 1) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 1) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 2) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 2) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 1) && (mat_lppl == 2) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 0) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 0) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 0) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 999;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 1) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 299;
                         	}                     	
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 1) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 1) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 2) && (mat_limite[0] == 0)) {
                         		loader.code[i][16] = 999;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 2) && (mat_limite[0] == 1)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                         	
                         	if ((mat_limite[1] == 2) && (mat_lppl == 2) && (mat_limite[0] == 2)) {
                         		loader.code[i][16] = 299;
                         		loader.code[i][12] = 299;
                         	}
                     	} else {
                     		if (loader.code[i][16] == 99) {
                     			loader.code[i][16] = 599;
                     		}
                     		
                     		if (loader.code[i][12] == 99) {
                     			loader.code[i][12] = 599;
                     		}
                     	}
                    } else {
                    	if (loader.code[i][16] == 99) {
                    		loader.code[i][16] = 599;
                    	}
                    	
                    	if (loader.code[i][16] == 29) {
                    		loader.code[i][16] = 529;
                    	}
                     	
                    	if (loader.code[i][12] == 99) {
                    		loader.code[i][12] = 599;
                    	}               	                 	
                    }
                } else {
                	if (loader.code[i][12] == 99) {
                		loader.code[i][12] = 599;
                	}
                	
                	if (loader.code[i][12] == 29) {
                		loader.code[i][12] = 529;
                	}
                    
                	if (loader.code[i][16] == 99) {
                		loader.code[i][16] = 599;
                	}             	               	
                }
            } else {
                if (loader.code[i][16] == 99) {
                	loader.code[i][16] = 599;
                }
                
                if (loader.code[i][16] == 29) {
                	loader.code[i][16] = 529;
                }
                
                if (loader.code[i][12] == 99) {
                    loader.code[i][12] = 599;
                }
                
                if (loader.code[i][12] == 29) {
                    loader.code[i][12] = 529;
                }         
            }
         	
            
         	
         	// End of the routine validation: Comparison of global radiation with par and lux level 3 
                     
            //----------------------------------------------------------------------------------------------------------------------
            
         	// Start of the routine validation: Global Radiation (W/m²) level 3
         	if ((loader.code[i][4] != 3333) && (loader.code[i][4] != -5555)  && (loader.code[i][4] != -6999) && (loader.code[i][4] != 552) && (loader.code[i][4] != 29)) {
         		if ((loader.code[i][8] != 3333) && (loader.code[i][8] != -5555) && (loader.code[i][8] != -6999) && (loader.code[i][8] != 552) && (loader.code[i][8] != 29) && 
         		     (loader.code[i][28] != 3333) && (loader.code[i][28] != -5555) && (loader.code[i][28] != -6999) && (loader.code[i][28] != 552) && (loader.code[i][28] != 29)) {
         				sumSw = loader.data[i][8] + (loader.data[i][28] * u0);
         				divSw = loader.data[i][4] / sumSw;
         				
         				
         				////////////////////////  TEST BSRN LEVEL 3 ////////////////////////
         				if (sumSw > 50) {
         					if (zenith_angle < 75) {
         						if ((divSw > 0.92) && (divSw < 1.08)) {
         							loader.code[i][4] = 999;
         						} else {
         							loader.code[i][4] = 299;
         						}
         					}
         					
         					if ((zenith_angle > 75) && (zenith_angle < 93)) {
         						if ((divSw > 0.85) && (divSw < 1.15)) {
         							loader.code[i][4] = 999;
         						} else {
         							loader.code[i][4] = 299;
         						}
         					}
         				} else {
         					loader.code[i][4] = 599;
         				}
         				
         				////////////////////////  TEST BSRN LEVEL 3 ////////////////////////
         				
         		} else { //////////////////////// Se a DIFUSA e DIRETA for RUIM - VERIFICA PAR E LUX
        			if (( (loader.code[i][12] != 529) && (loader.code[i][12] != 299) && (loader.code[i][12] != 552) && (loader.code[i][12] != 29) && 
                		  (loader.code[i][16] != 529) && (loader.code[i][16] != 299) && (loader.code[i][16] != 552) && (loader.code[i][16] != 29) )) {
         				  loader.code[i][4] = 999;
         			} else {
         				  loader.code[i][4] = 299;  
         			}   
         			
         			// Test 2
         			//loader.code[i][4] = 599;
         		}
         	} else {
         		if (loader.code[i][4] == 29) {
         			loader.code[i][4] = 529;
         		}
         	}
         	
         	// End of the routine validation: Global Radiation (W/m²) level 3
            
            //----------------------------------------------------------------------------------------------------------------------
         	
         	// Start of the routine validation: Diffuse Radiation (W/m²) level 3
         	if ((loader.code[i][8] != 3333) && (loader.code[i][8] != -5555) && (loader.code[i][8] != -6999) && (loader.code[i][8] != 552) && (loader.code[i][8] != 29)) {
         		if ((loader.code[i][4] != 3333) && (loader.code[i][4] != -5555) && (loader.code[i][4] != -6999) && (loader.code[i][4] != 552) && (loader.code[i][4] != 529)) {
         			if (loader.data[i][4] > 50) {
         				difSw = loader.data[i][8] / loader.data[i][4];
         				
         				if (zenith_angle < 75) {
         					if (difSw < 1.05) {
         						loader.code[i][8] = 999;
         					} else {
         						loader.code[i][8] = 299;
         					}
         				}
         				
         				if ((zenith_angle > 75) && (zenith_angle < 93)) {
         					if (difSw < 1.10) {
         						loader.code[i][8] = 999;
         					} else {
         						loader.code[i][8] = 299;
         					}
         				}
         			} else {
         				loader.code[i][8] = 599;
         			}
         		} else {
         			if (loader.code[i][8] == 99) {
         				loader.code[i][8] = 599;
         			}     			       			
         		}
         	} else {    		
         		if (loader.code[i][8] == 29) {
         			loader.code[i][8] = 529;
         		}    		
         	}
         	
         	// End of the routine validation: Diffuse Radiation (W/m²) level 3
                     
            //----------------------------------------------------------------------------------------------------------------------
         	    	
         	// Start of the routine validation: Air Temperature (°C) level 3
         	if ((loader.code[i][20] != 3333) && (loader.code[i][20] != -5555) && (loader.code[i][20] != 552) && (loader.code[i][20] != 529)) {
         		if (i <= totalTemp12h_1) {
            		int j= 0;
            		while (j <= temp12h) {
            			if ((loader.code[i + j][20] != 3333) && (loader.code[i + j][20] != 552) && (loader.code[i + j][20] != 529)) {
            				contTempValid++;
            			    
            				if (loader.data[i + j][20] > temp_max) {
            					temp_max = loader.data[i + j][20];
            				}
            				
            				if (loader.data[i + j][20] < temp_min) {
            					temp_min = loader.data[i + j][20];
            				}
            				
            				variation_temp12h = temp_max - temp_min;
            			}
            			j++;
            		}
            		
            		if (contTempValid >= 40) {
            			if (variation_temp12h > 0.5) {
            				loader.code[i][20] = 999;
            				lastTempValid = loader.code[i][20];
            			} else {
            				loader.code[i][20] = 299;
            			}
            		} else {
            			int l= 0;
            			while (l <= temp12h) {
            				if ((loader.code[i + l][20] != 3333) && (loader.code[i + l][20] != 552) && (loader.code[i + l][20] != 529)) {
            					if (i == 0) {
            						loader.code[i][20] = 559;
            					} else {
            						loader.code[i][20] = lastTempValid;
            					}
            				}
            				l++;
            			}
            			contTempValid = 0;
            			temp_max = 0;
            			temp_min = 999;
            		}
         		}
         		
         		if (i >= totalTemp12h_2) {
         			loader.code[i][20] = loader.code[totalTemp12h_1][20];
         		}
         	}
         	
         	// End of the routine validation: Air Temperature (°C) level 3
            
            //----------------------------------------------------------------------------------------------------------------------
         	
         	// Start of the routine validation: Accumulated Precipitation (mm) level 3
         	if ((loader.code[i][23] != 3333) && (loader.code[i][23] != -5555) && (loader.code[i][23] != 552) && (loader.code[i][23] != 529)) {
         		if (i <= totalPrec24h_1) {
         			int j= 0;
         			while (j <= prec24h) {
         				if ((loader.code[i + j][23] != 3333) && (loader.code[i + j][23] != 552) && (loader.code[i + j][23] != 529)) {
         					contPrecValid++;
         					
         					if (loader.data[i + j][23] > prec_max) {
         						prec_max = loader.data[i + j][23];
         					}
         					
         					if (loader.data[i + j][23] < prec_min) {
         						prec_min = loader.data[i + j][23];
         					}
         					
         					variation_prec24h = temp_max - prec_min;
         				}
         				j++;
         			}
         			
         			if (contPrecValid >= 40) {
         				if (variation_prec24h < 100) {
         					loader.code[i][23] = 999;
         					lastPrecValid = loader.code[i][23]; 
         				} else {
         					loader.code[i][23] = 299;
         				}
         			} else {
         				int l= 0;
         				while (l <= prec24h) {
         					if ((loader.code[i + l][23] != 3333) && (loader.code[i + l][23] != 552) && (loader.code[i + l][23] != 529)) {
         						if (i == 0) {
         							loader.code[i][23] = 559;
         						} else {
         							loader.code[i][23] = lastPrecValid;
         						}			
         					}
         					l++;
         				}
         				contPrecValid = 0;
         				prec_max = 0;
         				prec_min = 999;
         			}
            	}
         		
         		if (i >= totalPrec24h_2) {
         			loader.code[i][23] = loader.code[totalPrec24h_1][23];
         		}
         	}
         	
         	// End of the routine validation: Accumulated Precipitation (mm) level 3
                     
            //----------------------------------------------------------------------------------------------------------------------
         	
         	// Start of the routine validation: Wind Speed 10m (m/s) level 3
         	if ((loader.code[i][24] != 3333) && (loader.code[i][24] != -5555) && (loader.code[i][24] != 552) && (loader.code[i][24] != 529)) {
         		if (i <= totalWs1012h_1) {
         			int j= 0;
         			while (j <= ws1012h) {
         				if ((loader.code[i + j][24] != 3333) && (loader.code[i + j][24] != 552) && (loader.code[i + j][24] != 529)) {
         					contWspdValid++;
         					
         					if (loader.data[i + j][24] > ws10_max) {
         						ws10_max = loader.data[i + j][24];
         					}
         					
         					if (loader.data[i + j][24] < ws10_min) {
         						ws10_min = loader.data[i + j][24];
         					}
         					
         					variation_ws1012h = ws10_max - ws10_min;
         				}
         				j++;
         			}
         			
         			if (contWspdValid >= 40) {
         				if (variation_ws1012h > 0.5) {
         					loader.code[i][24] = 999;
         					lastWs10Valid = loader.code[i][24]; 
         				} else {
         					loader.code[i][24] = 299;
         				}
         			} else {
         				int l= 0;
         				while (l <= ws1012h) {
         					if ((loader.code[i + l][24] != 3333) && (loader.code[i + l][24] != 552) && (loader.code[i + l][24] != 529)) {
         						if (i == 0) {
         							loader.code[i][24] = 559;
         						} else {
         							loader.code[i][24] = lastWs10Valid;
         						}
         					}
         					l++;
         				}
         				contWspdValid = 0;
         				ws10_max = 0;
         				ws10_min = 999;
         			}
            	}
         		
         		if (i >= totalWs1012h_2) {
         			loader.code[i][24] = loader.code[totalWs1012h_1][24];
         		}
         	}
         
         	// End of the routine validation: Wind Speed 10m (m/s) level 3
                     
         	//----------------------------------------------------------------------------------------------------------------------
         
         	// Start of the routine validation: Wind Direction 10m (°) level 3
         	if ((loader.code[i][25] != 3333) && (loader.code[i][25] != -5555) && (loader.code[i][25] != 552) && (loader.code[i][25] != 529)) {
         		if (i <= totalWd1018h_1) {
         			int j= 0;
         			while (j <= wd1018h) {
         				if ((loader.code[i + j][25] != 3333) && (loader.code[i + j][25] != 552) && (loader.code[i + j][25] != 529)) {
         					contWdirValid++;
         					
         					if (loader.data[i + j][25] > wd10_max) {
         						wd10_max = loader.data[i + j][25];
         					}
         					
         					if (loader.data[i + j][25] < wd10_min) {
         						wd10_min = loader.data[i + j][25];
         					}
         					
         					variation_wd1018h = wd10_max - wd10_min;
         				}
         				j++;
         			}
         			
         			if (contWdirValid >= 40) {
         				if (variation_wd1018h > 10) {
         					loader.code[i][25] = 999;
         					lastWd10Valid = loader.code[i][25];
         				} else {
         					loader.code[i][25] = 299;
         				}
         			} else {
         				int l= 0;
         				while (l <= wd1018h) {
         					if ((loader.code[i + l][25] != 3333) && (loader.code[i + l][25] != 552) && (loader.code[i + l][25] != 529)) {
         						if (i == 0) {
         							loader.code[i][25] = 559;
         						} else {
         							loader.code[i][25] = lastWd10Valid;
         						}
         					}
         					l++;
         				}
         				contWdirValid = 0;
         				wd10_max = 0;
         				wd10_min = 100;
         			}
            	}
         		
         		if (i >= totalWd1018h_2) {
         			loader.code[i][25] = loader.code[totalWd1018h_1][25];
         		}
         	}
         	
         	// End of the routine validation: Wind Direction 10m (°) level 3
         	
         	//----------------------------------------------------------------------------------------------------------------------
         	
         	// Start of the routine validation: Direct Radiation (W/m²) level 3
         	if ((loader.code[i][28] != 3333) && (loader.code[i][28] != -5555) && (loader.code[i][28] != -6999) && (loader.code[i][28] != 552) && (loader.code[i][28] != 29)) {
         		if ((loader.code[i][8] != 3333) && (loader.code[i][8] != -5555) && (loader.code[i][8] != -6999) && (loader.code[i][8] != 552) && (loader.code[i][8] != 529)) {
         			if ((loader.code[i][4] != 3333) && (loader.code[i][4] != -5555) && (loader.code[i][4] != -6999) && (loader.code[i][4] != 552) && (loader.code[i][4] != 529)) {
         				direct_h = loader.data[i][4] - loader.data[i][8];
         				direct_n = (loader.data[i][28] * u0) - 50;
         				direct_p = (loader.data[i][28] * u0) + 50;
         				
         				if((direct_n <= direct_h) && (direct_h <= direct_p)) {
         					loader.code[i][28] = 999;
         				} else {
         					loader.code[i][28] = 299;
         				}
         			} else {
         				if (loader.code[i][28] == 99) {
         					loader.code[i][28] = 599;            				
         				}   				
         			}
         		} else {
         			if (loader.code[i][28] == 99) {
         				loader.code[i][28] = 599;
         			}   			         			
         		}
         	} else {
         		if (loader.code[i][28] == 29) {
     				loader.code[i][28] = 529;
     			}   		
         	}
         	
         	// End of the routine validation: Direct Radiation (W/m²) level 3
                     
            //----------------------------------------------------------------------------------------------------------------------
         	
         	// Start of the routine validation: Long Wave Radiation (W/m²) level 3
         	if ((loader.code[i][32] != 3333) && (loader.code[i][32] != -5555) && (loader.code[i][32] != -6999) && (loader.code[i][32] != 552) && (loader.code[i][32] != 29)) {
         		if ((loader.code[i][20] != 3333) && (loader.code[i][20] != -5555) && (loader.code[i][20] != 552) && (loader.code[i][20] != 529)) {
         			temp = loader.data[i][20] + 273.15;
         			temp_a = 0.4 * sigma * Math.pow(temp, 4);
         			temp_b = sigma * Math.pow(temp, 4) + 25;
         			
         			if ((temp_a < loader.data[i][32]) && (loader.data[i][32] < temp_b)) {
         				loader.code[i][32] = 999;
         			} else {
         				loader.code[i][32] = 299;
         			}
         		} else {
         			if (loader.code[i][32] == 99) {
         				loader.code[i][32] = 599;
         			}  			
         		}
         	} else {         		
         		if (loader.code[i][32] == 29) {
         			loader.code[i][32] = 529;         			
         		}
         	}
         			
         	// End of the routine validation: Long Wave Radiation (W/m²) level 3
                     
         	//----------------------------------------------------------------------------------------------------------------------
        } // End of loop level 3
    	return loader.code;
    }
}