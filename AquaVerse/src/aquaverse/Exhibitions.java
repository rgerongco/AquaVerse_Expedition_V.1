package aquaverse;
import java.io.*;
import java.util.Scanner;

public class Exhibitions {

    // Creates exhibits.txt and visitors.txt if they don't exist yet
    public static void createFiles() throws IOException {

        File visitors = new File("visitors.txt");
        if (!visitors.exists()) {
            visitors.createNewFile();
        }
    }

    // Reads exhibits.txt and lets the visitor view each category
    public static void showExhibitions(Scanner input) throws IOException {
        boolean done = false;
        boolean notDone = false;
        boolean notReallyDone = false;
        while (!done) {
            while (!notDone) {
                System.out.println("===== EXHIBITIONS =====");
                System.out.println("[1] Fish Exhibits");
                System.out.println("[2] Marine Animal Exhibits");
                System.out.println("[3] Coral Reef Exhibits");
                System.out.println("[4] Special Animal Habitats");
                System.out.println("[5] Interactive and Educational Areas");
                System.out.println("[0] Finish visit");
                System.out.print("Choose a category: ");
                int pick = input.nextInt();

                if (pick == 0) {
                    done = true;
                    break;
                }
                
                if (pick == 1) {
                    System.out.println("===== FISH EXHIBITS =====");
                    System.out.println("[1] Aranaima");
                    System.out.println("[2] Platinum Aronawa");
                    System.out.println("[3] Peppermint Angelfish");
                    System.out.println("[0] Exit Exhibit");
                    int choice = input.nextInt();
                    
                        if (choice == 0) {
                            break;
                        } else if (choice == 1) {
                            while (!notReallyDone) {
                                System.out.println("===== ARAPAIMA =====");
                                System.out.println("Description:  The Arapaima (Arapaima gigas) is the \nworld’s largest freshwater fish, featuring a dual-breathing \nsystem (gills and a lung-like swim bladder), reddish tail-end \nmarkings, and flavorful, nearly bone-free flesh.");
                                System.out.println("Diet: It is omnivorous, meaning it can feed on both \nplants and animals.");
                                System.out.println("Growth: Adult Arapaimas grow up to 3 meters long and \nreach a weight between 100 and 200kg.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 2) {
                            while (!notReallyDone) {
                                System.out.println("===== PLATINUM AROWANA =====");
                                System.out.println("Description: Asian Arowana and Dragon Fish are the \nother common names of this world’s most expensive. \nTheir average aquarium life is 20 years.");
                                System.out.println("Diet: Consists of meaty prey items taken at or \nnear the water's surface.");
                                System.out.println("Growth: They can grow up to 3 feet long in the wild.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 3) {
                            while (!notReallyDone) {
                                System.out.println("===== PEPPERMINT ANGELFISH =====");
                                System.out.println("Description: They are mysterious because it’s hard\n to reach their deep habitat. With proper care, they can\n live up to 20 years.");
                                System.out.println("Diet: Their diet consists of various foods: tunicates, detritus, \ncrustaceans, worms, sponges, algae, and frozen food.");
                                System.out.println("Growth: They can grow up to 3 inches in length.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                
                } else if (pick == 2) {
                    System.out.println("===== MARINE ANIMAL EXHIBITS =====");
                    System.out.println("[1] Pygmy Seahorse");
                    System.out.println("[2] Flamboyant Cuttlefish");
                    System.out.println("[3] Leafy Seadragon");
                    System.out.println("[0] Exit Exhibit");
                    int choice = input.nextInt();
                    
                        if (choice == 0) {
                            break;
                        } else if (choice == 1) {
                            while (!notReallyDone) {
                                System.out.println("===== PYGMY SEAHORSE =====");
                                System.out.println("Description: Recognized as one of the thinnest and cutest creatures\n in the sea, measuring no more than 1.7 cm length.");
                                System.out.println("Species: Includes several species such as bargibanti, pontohi, denise,\n and satomi.");
                                System.out.println("Habitat: Primarily found in Asia (including Indonesia, the Philippines,\n Malaysia, and Thailand) as well as the east coast of Australia.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 2) {
                            while (!notReallyDone) {
                                System.out.println("===== FLAMBOYANT CUTTLEFISH =====");
                                System.out.println("Description: Measures just 6 to 8 cm with a mostly dark brown body, moving along the sea floor by walking on its arms rather than swimming long distances.");
                                System.out.println("Photography: Makes for an incredible underwater photography subject for those who are able to spot them.");
                                System.out.println("Habitat: Primarily found in the Indian and Pacific Oceans, ranging from southern New Guinea to the north and west coasts of Australia.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 3) {
                            while (!notReallyDone) {
                                System.out.println("===== LEAFY SEADRAGON =====");
                                System.out.println("Description: Features leaf-like fins that mimic floating seaweed as it moves through the water, making it a master of camouflage.");
                                System.out.println("Photography: A favorite subject for underwater photographers; divers can spot them by searching around piers, kelp, and seagrass beds.");
                                System.out.println("Habitat: Native to the waters along the southern and western coasts of Australia.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        
                } else if (pick == 3) {
                    System.out.println("===== CORAL REEF EXHIBITS =====");
                    System.out.println("[1] Acropora Rongelapensis");
                    System.out.println("[2] Acropora Multiacuta");
                    System.out.println("[3] Australogyra Zelli");
                    System.out.println("[0] Exit Exhibit");
                    int choice = input.nextInt();
                    
                        if (choice == 0) {
                            break;
                        } else if (choice == 1) {
                            while (!notReallyDone) {
                                System.out.println("===== ACROPORA RONGELAPENSIS =====");
                                System.out.println("Description:  A critically rare and endangered branching scleractinian coral. Historically known almost exclusively from deep waters (28–32 meters) in the lagoon of Rongelap Atoll in the Marshall Islands, it is easily distinguished by its distinct laterally flattened branches and dense skeleton.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 2) {
                            while (!notReallyDone) {
                                System.out.println("===== ACROPORA MULTIACUTA =====");
                                System.out.println("Description: Spotted in Manado, Sulawesi, this rare and endangered Acropora species is characterized by its large, thick branch groupings featuring elongated, sharply tapering axial corallites that give it a distinctive unicorn shape.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 3) {
                            while (!notReallyDone) {
                                System.out.println("===== AUSTRALOGYRA ZELLI =====");
                                System.out.println("Description: An unusual and under-appreciated coral species that breaks from typical moon coral growth forms by forming distinct, delicate branching structures rather than massive or encrusting colonies.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        
                } else if (pick == 4) {
                    System.out.println("===== SPECIAL ANIMAL HABITATS =====");
                    System.out.println("[1] Nautilus");
                    System.out.println("[2] Laura's Limu");
                    System.out.println("[3] Green Lunar Sponge");
                    System.out.println("[0] Exit Exhibit");
                    int choice = input.nextInt();
                    
                        if (choice == 0) {
                            break;
                        } else if (choice == 1) {
                            while (!notReallyDone) {
                                System.out.println("===== NAUTILUS =====");
                                System.out.println("Description:  Nautilus samoaensis is a newly identified nautilus species (officially described in 2023) native to deep waters (~1,200 feet) within the National Marine Sanctuary of American Samoa.");
                                System.out.println("Characteristics: An ancient relative of octopuses and squids, this mollusk features a spiraled external shell and up to 90 tentacles.");
                                System.out.println("Research & Conservation: Researchers are tracking its movements near Tutuila Island to study its ecology. Though specific population data is limited, it is considered rare and likely faces population decline from human activity similar to other nautilus species.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 2) {
                            while (!notReallyDone) {
                                System.out.println("===== LAURA'S LIMU =====");
                                System.out.println("Description: Croisettea kalaukapuae (Laura's limu) is a newly described deep-reef alga found exclusively in the remote waters of Papahānaumokuākea National Marine Sanctuary.");
                                System.out.println("Cultural Significance: It was named in honor of Laura Kalaukapu Thompson, a dedicated advocate for conservation and Hawaiian cultural preservation.");
                                System.out.println("Ecological Value: Its unique structure and role in deep-reef ecosystems highlight the sanctuary's crucial role in protecting rare, undiscovered marine biodiversity for future research.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 3) {
                            while (!notReallyDone) {
                                System.out.println("===== GREEN LUNAR SPONGE =====");
                                System.out.println("Description: Nautilus samoaensis is a newly identified nautilus species (officially described in 2023) native to deep waters (~1,200 feet) within the National Marine Sanctuary of American Samoa.");
                                System.out.println("Characteristics: An ancient relative of octopuses and squids, this mollusk features a spiraled external shell and up to 90 tentacles.");
                                System.out.println("Research & Conservation: Researchers are tracking its movements near Tutuila Island to study its ecology. Though specific population data is limited, it is considered rare and likely faces population decline from human activity similar to other nautilus species.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        
                } else if (pick == 5) {
                    System.out.println("===== INTERACTIVE AND EDUCATIONAL AREAS =====");
                    System.out.println("[1] Aquarium Wall");
                    System.out.println("[2] Interactive Marine Life Wall");
                    System.out.println("[3] Marine Conservation Learning Station");
                    System.out.println("[0] Exit Exhibit");
                    int choice = input.nextInt();
                    
                        if (choice == 0) {
                            break;
                        } else if (choice == 1) {
                            while (!notReallyDone) {
                                System.out.println("===== AQUARIUM WALL =====");
                                System.out.println("Description: This helps visitors to have a better view and experience.");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 2) {
                            while (!notReallyDone) {
                                System.out.println("===== INTERACTIVE MARINE LIFE WALL =====");
                                System.out.println("Description: A digital display where visitors tap different animals to learn their names, diets, habitats, and conservation status. ");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else if (choice == 3) {
                            while (!notReallyDone) {
                                System.out.println("===== MARINE CONSERVATION LEARNING STATION =====");
                                System.out.println("Description: Visitors participate in quizzes, identify pollution sources, or learn how to protect marine ecosystems");
                                System.out.println("[0] Exit");
                                choice = input.nextInt();
                                if (choice == 0) {
                                    notReallyDone = true;
                                    break;
                                }  
                            }
                            notReallyDone = false;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
    }
}
