/*
 * Keegan Clayton
 * CIS2901
 * 4/28/2019
 * This application allows for the storage & management of card data, ability data,
 * hero data, and deck data. It stores entries that hold information such as the name
 * of a card or what an ability does (its 'effect')
 */

package card.database.pack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainBuilderGUI extends Application {

	String currPane = "";

	public static void main(String[] args) {launch(args);}

	Font font = new Font(12);

	// Variables for search, modification, & insertion
			static boolean error = false;
			int cardNum = 0;
			int abilNum = 0;
			int heroNum = 0;
			int deckNum = 0;
			String cardName = "";
			String abilName = "";
			String heroName = "";
			String deckName = "";
			int cType = 0; // The type of card it is (A summon/creature that fights, a spell, a trap, or an aura)
			int cAttack = 0; // The attack of the summon-type card (only relevant if it's a summon)
			int cHealth = 0; // The health of the summon-type card
			String tagName = "";
			int rLvl = 0;
			int eType = 0; // What the effect is/does
			int ePower = 0; // How strong the effect is
			int ePower2 = 0; // Only applied to the 'Buff' effect
			int uType = 0; // When the summon-type card may trigger its effect
			int pPT = 1; // Points per turn; how much a hero will gain to spend each turn
			int health = 50; // The health value for the hero
			int attack = 0; // The attack value for the hero
			int regen = 0; // The per-turn health regeneration of the hero
			StringBuilder traitsSB;
			String traits = ""; // For heroes; not currently relevant
			StringBuilder cardsSB;
			String cards = ""; // The cards within a deck
			double dValue = 0; // The total value of all the cards in a deck combined
			// Database variables & more
			// Connection to the database
			private static Connection connection;
			// Statement to execute SQL commands
			private Statement statement;
			private Label lblConnectionStatus = new Label("No connection now");
	/**/

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Establish DB connection
		try{
	        connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "MainUser", "Password");
	    }
	    catch (SQLException ex) {
	    		System.out.println("Error creating DB connection");
	    		ex.printStackTrace();
	    }

		// ???
		// Labels & more
		Label lblName = new Label("Name: ");
		TextField txtName = new TextField();
		// Card type Label & ComboBox (drop-down list)
		String [] cTypes = {"Summon", "Spell", "Aura", "Trap"};
		Label lblCTypeBox = new Label("Select card type: ");
		ComboBox<String> cboCTypeBox = new ComboBox<String>();
		cboCTypeBox.getItems().addAll(cTypes);
		cboCTypeBox.setValue(cTypes[0]);
		// Main menu buttons
		Button btCreateCard = new Button("               Create a card                ");
		Button btEditCard = new Button("               Edit a card                ");
		Button btCreateAbil = new Button("             Create an ability             ");
		Button btEditAbil = new Button("             Edit an ability              ");
		Button btCreateHero = new Button("               Create a hero                ");
		Button btEditHero = new Button("               Edit a hero                ");
		Button btCreateDeck = new Button("               Create a deck                ");
		Button btEditDeck = new Button("               Edit a deck                ");

		/* The following is for the user to be able to
		 * enter or change data to be placed in a database entry
		 * on the different screens in the application
		 */
		// Create a card stuff \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
		Label lblAttack = new Label("Attack: " + cAttack);
		 Button btAttackUp = new Button("^");
		 Button btAttackDn = new Button("v");
		Label lblHealth = new Label("Health: " + cHealth);
		 Button btHealthUp = new Button("^");
		 Button btHealthDn = new Button("v");
		Label lblTagName = new Label("Tag Name: ");
		 TextField txtTagName = new TextField();
		Label lblRarity = new Label("Rarity: ");
		 String [] rLevels = {"0", "1", "2", "3"};
		 ComboBox<String> cboRLevel = new ComboBox<String>();
		 cboRLevel.getItems().addAll(rLevels);
		 cboRLevel.setValue(rLevels[0]);
		Label lblEType = new Label("Effect Type: ");
		 String [] eTypes = {"Armor", "Buff", "Damage", "DamageSummon", "DestroyAura",
				 "DestroySpell", "DestroySummon", "DestroyTrap", "Disable", "Draw",
				 "Frenzy", "Gain", "Guard", "Heal", "Rush", "Shield", "Spellimmune",
				 "Stun", "OpponentDraw", "Take"};
		 ComboBox<String> cboEType = new ComboBox<String>();
		 cboEType.getItems().addAll(eTypes);
		 cboEType.setValue(eTypes[0]);
		Label lblEPower = new Label("Effect Power: ");
		 Button btEPowerUp = new Button("^");
		 Button btEPowerDn = new Button("v");
		Label lblEPower2 = new Label("Effect Power 2: ");
		 Button btEPower2Up = new Button("^");
		 Button btEPower2Dn = new Button("v");
		Label lblEUsage = new Label("Effect Usage: ");
		 String [] eUTypes = {"OnSummon", "Castable", "OnDeath"};
		 ComboBox<String> cboEUsage = new ComboBox<String>();
		 cboEUsage.getItems().addAll(eUTypes);
		 cboEUsage.setValue(eUTypes[0]);
		/* Edit a card stuff: */Label lblCardNum = new Label("Card Number: " + cardNum);

		// Create/Edit an ability stuff \/\/
		Label lblAbilVal = new Label("Ability value: ");
		Label lblAbilNum = new Label("Abliity Number: " + abilNum);

		// Create/Edit a hero stuff \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
		Label lblHeroAbil = new Label("Hero Ability: ");
		TextField txtHeroAbil = new TextField();
		/*String [] hAbils = {""};
		 ComboBox<String> cboHAbils = new ComboBox<String>();
		 cboHAbils.getItems().addAll(hAbils);
		 cboHAbils.setValue(hAbils[0]);*/
		Label lblPPT = new Label("Points per turn: " + pPT);
		 Button btPPTUp = new Button("^");
		 Button btPPTDn = new Button("v");
		Label lblAttackH = new Label("Attack: " + attack);
		 Button btAttackUpH = new Button("^");
		 Button btAttackDnH = new Button("v");
		Label lblHealthH = new Label("Health: " + health);
		 Button btHealthUpH = new Button("^");
		 Button btHealthDnH = new Button("v");
		Label lblRegen = new Label("Health Regen: " + regen);
		 Button btRegenUp = new Button("^");
		 Button btRegenDn = new Button("v");
		Label lblHeroTraits = new Label("Hero Traits: ");
		Label lblHeroNum = new Label("Hero Number: " + heroNum);
		// Checkboxes for some, #'s & arrows for others

		// Create/Edit deck stuff \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
		Label lblHeroName = new Label("Hero: ");
		/*String [] hList = {selectAllHeroes()};
		 ComboBox<String> cboHList = new ComboBox<String>();
		 cboHList.getItems().addAll(hList);
		 cboHList.setValue(hList[0]);*/
		TextField txtHeroName = new TextField();
		Label lblCards = new Label("Cards: " + cards);
		Label lblDValue = new Label("Total Deck Value: " + dValue);
		//String [] cList = {};
		Label lblDeckNum = new Label("Deck Number: " + deckNum);

		// Search/edit stuff \/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
		Label lblSrchManual = new Label("Search manually: ");
		Button btSearch = new Button("     Search     ");
		Button btSearchUp = new Button("^");
		Button btSearchDn = new Button("v");
		Button btSearchUp10 = new Button("^ 10");
		Button btSearchDn10 = new Button("v 10");

		// Bottom buttons+ (Finish, Edit, Delete, Cancel, Exit) \/\/\/\/\/\/\/\/\/\/\/\/
		Label lblError = new Label("Error: Not Found");
		Button btFinish = new Button("     Finish     ");
		Button btEdit = new Button("      Edit      ");
		Button btUpdate = new Button("     Update     ");
		Button btDelete = new Button("     Delete     ");
		Button btCancel = new Button("     Cancel     ");
		Button btExit = new Button("                Exit                ");


		// The following code builds the actual screens/menus
		// MAIN MENU
		GridPane paneMM = new GridPane();
		paneMM.setPadding(new Insets(10));paneMM.setVgap(10);paneMM.setHgap(5);
		paneMM.add(btCreateCard, 0, 2);paneMM.add(btEditCard, 1, 2);
		paneMM.add(btCreateAbil, 0, 4);paneMM.add(btEditAbil, 1, 4);
		paneMM.add(btCreateHero, 0, 6);paneMM.add(btEditHero, 1, 6);
		paneMM.add(btCreateDeck, 0, 8);paneMM.add(btEditDeck, 1, 8);
		paneMM.add(btExit, 0, 10);

		// CREATE CARD MENU
		GridPane paneCC = new GridPane();
		paneCC.setPadding(new Insets(10));paneCC.setVgap(1);paneCC.setHgap(0);
		paneCC.add(lblCTypeBox, 0, 6);paneCC.add(cboCTypeBox, 2, 6);
		paneCC.add(lblAttack, 0, 8);paneCC.add(btAttackUp, 2, 8);paneCC.add(btAttackDn, 4, 8);
		paneCC.add(lblHealth, 0, 10);paneCC.add(btHealthUp, 2, 10);paneCC.add(btHealthDn, 4, 10);
		paneCC.add(lblTagName, 0, 12);paneCC.add(txtTagName, 2, 12);
		paneCC.add(lblRarity, 0, 14);paneCC.add(cboRLevel, 2, 14);
		paneCC.add(lblEUsage, 0, 22);paneCC.add(cboEUsage, 2, 22);

		// EDIT CARD MENU
		GridPane paneEC = new GridPane();
		paneEC.setPadding(new Insets(10));paneEC.setVgap(1);paneEC.setHgap(0);
		paneEC.add(lblCardNum, 0, 10);

		// CREATE ABILITY MENU
		GridPane paneCA = new GridPane();
		paneCA.setPadding(new Insets(10));paneCA.setVgap(1);paneCA.setHgap(0);
		paneCA.add(lblAbilVal, 0, 8);

		// EDIT ABILITY MENU
		GridPane paneEA = new GridPane();
		paneEA.setPadding(new Insets(10));paneEA.setVgap(1);paneEA.setHgap(0);
		paneEA.add(lblAbilNum, 0, 4);

		// CREATE HERO MENU
		GridPane paneCH = new GridPane();
		paneCH.setPadding(new Insets(10));paneCH.setVgap(1);paneCH.setHgap(0);
		paneCH.add(lblHeroAbil, 0, 2);paneCH.add(txtHeroAbil, 2, 2);
		paneCH.add(lblAttackH, 0, 4);paneCH.add(btAttackUpH, 2, 4);paneCH.add(btAttackDnH, 4, 4);
		paneCH.add(lblHealthH, 0, 6);paneCH.add(btHealthUpH, 2, 6);paneCH.add(btHealthDnH, 4, 6);
		paneCH.add(lblPPT, 0, 8);paneCH.add(btPPTUp, 2, 8);paneCH.add(btPPTDn, 4, 8);
		paneCH.add(lblRegen, 0, 10);paneCH.add(btRegenUp, 2, 10);paneCH.add(btRegenDn, 4, 10);
		//paneCH.add(lblTraits, 0, 12); //Doesn't exist right now

		// EDIT HERO MENU
		GridPane paneEH = new GridPane();
		paneEH.setPadding(new Insets(10));paneEH.setVgap(1);paneEH.setHgap(0);
		paneEH.add(lblHeroNum, 0, 10);

		// CREATE DECK MENU
		GridPane paneCD = new GridPane();
		paneCD.setPadding(new Insets(10));paneCD.setVgap(1);paneCD.setHgap(0);
		paneCD.add(lblHeroName, 0, 2);paneCD.add(txtHeroName, 2, 2);
		paneCD.add(lblCards, 0, 4);
		paneCD.add(lblDValue, 0, 10);

		// EDIT DECK MENU
		GridPane paneED = new GridPane();
		paneED.setPadding(new Insets(10));paneED.setVgap(1);paneED.setHgap(0);
		paneED.add(lblDeckNum, 0, 14);

		GridPane paneNO = new GridPane();
		// SET THE SCENE/SCREEN
		Scene sceneMain = new Scene(paneMM, 450, 400);
		primaryStage.setTitle("Card Game Database"); // Set the stage title
		primaryStage.setScene(sceneMain); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		// The event handling code for when the user clicks on one of the buttons on the main menu
		btCreateCard.setOnAction(e -> {primaryStage.getScene().setRoot(paneCC);
				currPane = "paneCC";
				paneCC.add(lblName, 0, 0);paneCC.add(txtName, 2, 0);
				paneNO.add(btFinish, 2, 24);paneCC.add(btFinish, 0, 24);
				paneNO.add(btCancel, 2, 24);paneCC.add(btCancel, 2, 24);
				paneNO.add(cboEType, 0, 0);paneCC.add(lblEType, 0, 16);paneCC.add(cboEType, 2, 16);
				paneCC.add(lblEPower, 0, 18);paneCC.add(btEPowerUp, 2, 18);paneCC.add(btEPowerDn, 4, 18);
				paneCC.add(lblEPower2, 0, 20);paneCC.add(btEPower2Up, 2, 20);paneCC.add(btEPower2Dn, 4, 20);
		});
		btEditCard.setOnAction(e -> {primaryStage.getScene().setRoot(paneEC);
				currPane = "paneEC";
				paneEC.add(lblName, 0, 0);paneEC.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneEC.add(btCancel, 4, 24);
				paneEC.add(btEdit, 0, 24);paneEC.add(btDelete, 2, 24);
				// Manual search buttons
				paneEC.add(lblSrchManual, 0, 2);paneEC.add(btSearchUp, 1, 2);paneEC.add(btSearchDn, 2, 2);paneEC.add(btSearchUp10, 3, 2);paneEC.add(btSearchDn10, 4, 2);
				});
		btCreateAbil.setOnAction(e -> {primaryStage.getScene().setRoot(paneCA);
				currPane = "paneCA";
				paneNO.add(txtName, 0, 0);paneCA.add(lblName, 0, 0);paneCA.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneCA.add(btCancel, 2, 24);
				paneNO.add(btFinish, 2, 24);paneCA.add(btFinish, 0, 24);
				paneCA.add(lblEType, 0, 2);paneCA.add(cboEType, 2, 2);
				paneCA.add(lblEPower, 0, 4);paneCA.add(btEPowerUp, 2, 4);paneCA.add(btEPowerDn, 4, 4);
				paneCA.add(lblEPower2, 0, 6);paneCA.add(btEPower2Up, 2, 6);paneCA.add(btEPower2Dn, 4, 6);
				});
		btEditAbil.setOnAction(e -> {primaryStage.getScene().setRoot(paneEA);
				currPane = "paneEA";
				paneNO.add(txtName, 0, 0);paneEA.add(lblName, 0, 0);paneEA.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneEA.add(btCancel, 4, 24);
				paneEA.add(btEdit, 0, 24);paneEA.add(btDelete, 2, 24);
				// Manual search
				paneEA.add(lblSrchManual, 0, 2);paneEA.add(btSearchUp, 1, 2);paneEA.add(btSearchDn, 2, 2);paneEA.add(btSearchUp10, 3, 2);paneEA.add(btSearchDn10, 4, 2);
				});
		btCreateHero.setOnAction(e -> {primaryStage.getScene().setRoot(paneCH);
				currPane = "paneCH";
				paneNO.add(txtName, 0, 0);paneCH.add(lblName, 0, 0);paneCH.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneCH.add(btCancel, 2, 24);
				paneNO.add(btFinish, 2, 24);paneCH.add(btFinish, 0, 24);
				});
		btEditHero.setOnAction(e -> {primaryStage.getScene().setRoot(paneEH);
				currPane = "paneEH";
				paneNO.add(txtName, 0, 0);paneEH.add(lblName, 0, 0);paneEH.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneEH.add(btCancel, 4, 24);
				paneEH.add(btEdit, 0, 24);paneEH.add(btDelete, 2, 24);
				// Manual search
				paneEH.add(lblSrchManual, 0, 2);paneEH.add(btSearchUp, 1, 2);paneEH.add(btSearchDn, 2, 2);paneEH.add(btSearchUp10, 3, 2);paneEH.add(btSearchDn10, 4, 2);
				});
		btCreateDeck.setOnAction(e -> {primaryStage.getScene().setRoot(paneCD);
				currPane = "paneCD";
				paneNO.add(txtName, 0, 0);paneCD.add(lblName, 0, 0);paneCD.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneCD.add(btCancel, 2, 24);
				paneNO.add(btFinish, 2, 24);paneCD.add(btFinish, 0, 24);
				});
		btEditDeck.setOnAction(e -> {primaryStage.getScene().setRoot(paneED);
				currPane = "paneED";
				paneNO.add(txtName, 0, 0);paneED.add(lblName, 0, 0);paneED.add(txtName, 2, 0);
				paneNO.add(btCancel, 2, 24);paneED.add(btCancel, 4, 24);
				paneED.add(btEdit, 0, 24);paneED.add(btDelete, 2, 24);
				// Manual search
				paneED.add(lblSrchManual, 0, 2);paneED.add(btSearchUp, 1, 2);paneED.add(btSearchDn, 2, 2);paneED.add(btSearchUp10, 3, 2);paneED.add(btSearchDn10, 4, 2);
				});
		// To back out of a screen into the main menu
		btCancel.setOnAction(e -> {primaryStage.getScene().setRoot(paneMM);
				if(currPane == "paneCC" || currPane == "paneCA" || currPane == "paneCH" || currPane == "paneCD")
					{paneNO.add(btUpdate, 2, 24);}
				});

		// For when searching for an entry by number is implemented
		btSearchUp.setOnAction(e -> {
				if (currPane == "paneEC") {cardNum += 1; lblCardNum.setText("Card Number: " + cardNum);}
				else if (currPane == "paneEA") {abilNum += 1; lblAbilNum.setText("Ability Number: " + abilNum);}
				else if (currPane == "paneEH") {heroNum += 1; lblHeroNum.setText("Hero Number: " + heroNum);}
				else if (currPane == "paneED") {deckNum += 1; lblDeckNum.setText("Deck Number: " + deckNum);}
				else {/* Something went wrong; see a doctor */}
				});
		btSearchDn.setOnAction(e -> {
				if (currPane == "paneEC") {cardNum -= 1; lblCardNum.setText("Card Number: " + cardNum);}
				else if (currPane == "paneEA") {abilNum -= 1; lblAbilNum.setText("Ability Number: " + abilNum);}
				else if (currPane == "paneEH") {heroNum -= 1; lblHeroNum.setText("Hero Number: " + heroNum);}
				else if (currPane == "paneED") {deckNum -= 1; lblDeckNum.setText("Deck Number: " + deckNum);}
				else {/* Something went wrong */}
				});
		btSearchUp10.setOnAction(e -> {
				if (currPane == "paneEC") {cardNum += 10; lblCardNum.setText("Card Number: " + cardNum);}
				else if (currPane == "paneEA") {abilNum += 10; lblAbilNum.setText("Ability Number: " + abilNum);}
				else if (currPane == "paneEH") {heroNum += 10; lblHeroNum.setText("Hero Number: " + heroNum);}
				else if (currPane == "paneED") {deckNum += 10; lblDeckNum.setText("Deck Number: " + deckNum);}
				else {/* Something went wrong */}
				});
		btSearchDn10.setOnAction(e -> {
				if (currPane == "paneEC") {cardNum -= 10; lblCardNum.setText("Card Number: " + cardNum);}
				else if (currPane == "paneEA") {abilNum -= 10; lblAbilNum.setText("Ability Number: " + abilNum);}
				else if (currPane == "paneEH") {heroNum -= 10; lblHeroNum.setText("Hero Number: " + heroNum);}
				else if (currPane == "paneED") {deckNum -= 10; lblDeckNum.setText("Deck Number: " + deckNum);}
				else {/* Something went wrong */}
				});

		// The up & down arrow buttons for increasing and decreasing values to be entered in the database
		btAttackUp.setOnAction(e -> {if (cAttack < 99) {cAttack += 1;lblAttack.setText("Attack: " + cAttack);}});
		btAttackDn.setOnAction(e -> {if (cAttack > 0) {cAttack -= 1;lblAttack.setText("Attack: " + cAttack);}});
		btHealthUp.setOnAction(e -> {if (cHealth < 100) {cHealth += 1;lblHealth.setText("Health: " + cHealth);}});
		btHealthDn.setOnAction(e -> {if (cHealth > 1) {cHealth -= 1;lblHealth.setText("Health: " + cHealth);}});
		btPPTUp.setOnAction( e -> {if (pPT < 6) {pPT += 1;lblPPT.setText("Points Per Turn: " + pPT);}});
		btPPTDn.setOnAction(e -> {if (pPT > 1) {pPT -= 1;lblPPT.setText("Points Per Turn: " + pPT);}});
		btAttackUpH.setOnAction(e -> {if (attack < 5) {attack += 1;lblAttackH.setText("Attack: " + attack);}});
		btAttackDnH.setOnAction(e -> {if (attack > 0) {attack -= 1;lblAttackH.setText("Attack: " + attack);}});
		btRegenUp.setOnAction(e -> {if (regen < 5) {regen += 1;lblRegen.setText("Health Regen: " + regen);}});
		btRegenDn.setOnAction(e -> {if (regen > 0) {regen -= 1;lblRegen.setText("Health Regen: " + regen);}});
		btHealthUpH.setOnAction(e -> {if (health < 100) {health += 10;lblHealthH.setText("Health: " + health);}});
		btHealthDnH.setOnAction(e -> {if (health > 50) {health -= 10;lblHealthH.setText("Health: " + health);}});
		btEPowerUp.setOnAction(e -> {if (ePower < 50) {ePower += 1;lblEPower.setText("Effect Power: " + ePower);}});
		btEPowerDn.setOnAction(e -> {if (ePower > 0) {ePower -= 1;lblEPower.setText("Effect Power: " + ePower);}});
		btEPower2Up.setOnAction(e -> {if (ePower2 < 50) {ePower2 += 1;lblEPower2.setText("Effect Power 2: " + ePower2);}});
		btEPower2Dn.setOnAction(e -> {if (ePower2 > 0) {ePower2 -= 1;lblEPower2.setText("Effect Power 2: " + ePower2);}});

		//A special spot for special EventHandler's
		EventHandler handlerFinish = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (currPane == "paneCC") {
					// If it's a card's data, input a new card entry into the database
					cardName = txtName.getText();
					cType = cboCTypeBox.getSelectionModel().getSelectedIndex();
					tagName = txtTagName.getText();
					rLvl = cboRLevel.getSelectionModel().getSelectedIndex();
					eType = cboEType.getSelectionModel().getSelectedIndex();
					insertRowCards(cardName, cType, cAttack, cHealth, tagName, rLvl, eType, ePower, ePower2, uType);
					primaryStage.getScene().setRoot(paneMM);
					System.out.println("Finished");
				}
				else if (currPane == "paneCA") {
					// If it's an ability's data, input a new ability entry into the database
					abilName = txtName.getText();
					eType = cboEType.getSelectionModel().getSelectedIndex();
					insertRowAbilities(abilName, eType, ePower, ePower2);
					primaryStage.getScene().setRoot(paneMM);
					System.out.println("Finished");
				}
				else if (currPane == "paneCH") {
					// If it's a hero's data, input a new hero entry into the database
					heroName = txtName.getText();
					abilName = txtHeroAbil.getText();
					//traits = ???; a whole list, most likely (loop?)
					insertRowHeroes(heroName, abilName, pPT, health, attack, regen, traits);
					primaryStage.getScene().setRoot(paneMM);
					System.out.println("Finished");
				}
				else if (currPane == "paneCD") {
					// If it's a deck's data, input a new deck entry into the database
					deckName = txtName.getText();
					heroName = txtHeroName.getText();
					//??? Array of 40 cards should be here; loop may be required
					insertRowDecks(deckName, heroName, cards, dValue);
					primaryStage.getScene().setRoot(paneMM);
					System.out.println("Finished");
				}
				else {/* Something went wrong */}
			}
		};
		// Close the application
		EventHandler handlerExit = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("Exited Application"); Platform.exit();
			}
		};
		/* Move into the appropriate creation screen with fields already filled with
		 * the data of the entry chosen on the edit screen and an update button in place
		 * of the finish button
		 */
		EventHandler handlerEdit = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (currPane == "paneEC") {primaryStage.getScene().setRoot(paneCC);
					// If it's the card editing screen
					currPane = "paneCC";
					paneCC.add(lblName, 0, 0);paneCC.add(txtName, 2, 0);
					paneCC.add(btUpdate, 0, 24);paneCC.add(btCancel, 2, 24);
					paneCC.add(lblEType, 0, 16);paneCC.add(cboEType, 2, 16);
					paneCC.add(lblEPower, 0, 18);paneCC.add(btEPowerUp, 2, 18);paneCC.add(btEPowerDn, 4, 18);
					paneCC.add(lblEPower2, 0, 20);paneCC.add(btEPower2Up, 2, 20);paneCC.add(btEPower2Dn, 4, 20);
					// Direct/Name search function
					selectRowCards(txtName.getText());
					if (error == true) {
						if (txtName.getText() != "") {txtName.setText("");} else {txtName.setText("Name");}
						primaryStage.getScene().setRoot(paneMM);
						if(currPane == "paneCC" || currPane == "paneCA" || currPane == "paneCH" || currPane == "paneCD")
							{paneNO.add(btUpdate, 2, 24);}
						//^^^ as if cancel button was pressed
						error = false;}
				}
				else if (currPane == "paneEA") {primaryStage.getScene().setRoot(paneCA);
					// If it's the ability editing screen
					currPane = "paneCA";
					paneCA.add(lblName, 0, 0);paneCA.add(txtName, 2, 0);
					paneCA.add(btCancel, 2, 24);paneCA.add(btUpdate, 0, 24);
					paneCA.add(lblEType, 0, 2);paneCA.add(cboEType, 2, 2);
					paneCA.add(lblEPower, 0, 4);paneCA.add(btEPowerUp, 2, 4);paneCA.add(btEPowerDn, 4, 4);
					paneCA.add(lblEPower2, 0, 6);paneCA.add(btEPower2Up, 2, 6);paneCA.add(btEPower2Dn, 4, 6);
					// Direct/Name search function
					selectRowAbilities(txtName.getText());

					if (error == true) {
						if (txtName.getText() != "") {txtName.setText("");} else {txtName.setText("Name");}
						primaryStage.getScene().setRoot(paneMM);
						if(currPane == "paneCC" || currPane == "paneCA" || currPane == "paneCH" || currPane == "paneCD")
							{paneNO.add(btUpdate, 2, 24);}
						//^^^ as if cancel button was pressed
						error = false;}
				}
				else if (currPane == "paneEH") {primaryStage.getScene().setRoot(paneCH);
					// If it's the hero editing screen
					currPane = "paneCH";
					paneCH.add(lblName, 0, 0);paneCH.add(txtName, 2, 0);
					paneCH.add(btCancel, 2, 24);paneCH.add(btUpdate, 0, 24);
					// Direct/Name search function
					selectRowHeroes(txtName.getText());

					if (error == true) {
						if (txtName.getText() != "") {txtName.setText("");} else {txtName.setText("Name");}
						primaryStage.getScene().setRoot(paneMM);
						if(currPane == "paneCC" || currPane == "paneCA" || currPane == "paneCH" || currPane == "paneCD")
							{paneNO.add(btUpdate, 2, 24);}
						//^^^ as if cancel button was pressed
						error = false;}
				}
				else if (currPane == "paneED") {primaryStage.getScene().setRoot(paneCD);
					// If it's the deck editing screen
					currPane = "paneCD";
					paneCD.add(lblName, 0, 0);paneCD.add(txtName, 2, 0);
					paneCD.add(btCancel, 2, 24);paneCD.add(btUpdate, 0, 24);
					// Direct/Name search function
					selectRowDecks(txtName.getText());

					if (error == true) {
						if (txtName.getText() != "") {txtName.setText("");} else {txtName.setText("Name");}
						primaryStage.getScene().setRoot(paneMM);
						if(currPane == "paneCC" || currPane == "paneCA" || currPane == "paneCH" || currPane == "paneCD")
							{paneNO.add(btUpdate, 2, 24);}
						//^^^ as if cancel button was pressed
						error = false;}
				}
				else {/* Something went wrong */}
			}
		};
		EventHandler handlerUpdate = new EventHandler<ActionEvent>() { // Replace the chosen entry's data
			public void handle(ActionEvent e) {
				if (currPane == "paneCC") {
					// If it's a card's data
					cardName = txtName.getText();
					cType = cboCTypeBox.getSelectionModel().getSelectedIndex();
					tagName = txtTagName.getText();
					rLvl = cboRLevel.getSelectionModel().getSelectedIndex();
					eType = cboEType.getSelectionModel().getSelectedIndex();
					updateRowCards(cardName, cType, cAttack, cHealth, tagName, rLvl, eType, ePower, ePower2, uType);
					paneNO.add(btFinish, 2, 24);paneCC.add(btFinish, 0, 24);paneNO.add(btCancel, 2, 24);paneCC.add(btCancel, 2, 24);
					paneNO.add(btUpdate, 0, 0);
					primaryStage.getScene().setRoot(paneMM);
					System.out.println("Finished");
				}
				else if (currPane == "paneCA") {
					// If it's an ability's data
					abilName = txtName.getText();
					eType = cboEType.getSelectionModel().getSelectedIndex();
					updateRowAbilities(abilName, eType, ePower, ePower2);
					primaryStage.getScene().setRoot(paneMM);
					paneNO.add(btFinish, 2, 24);paneCA.add(btFinish, 0, 24);paneNO.add(btCancel, 2, 24);paneCA.add(btCancel, 2, 24);
					paneNO.add(btUpdate, 0, 0);
					System.out.println("Finished");
				}
				else if (currPane == "paneCH") {
					// If it's a hero's data
					heroName = txtName.getText();
					abilName = txtHeroAbil.getText();
					//traits = ???; a whole list, most likely (loop?)
					updateRowHeroes(heroName, abilName, pPT, health, attack, regen, traits);
					primaryStage.getScene().setRoot(paneMM);
					paneNO.add(btFinish, 2, 24);paneCH.add(btFinish, 0, 24);paneNO.add(btCancel, 2, 24);paneCH.add(btCancel, 2, 24);
					paneNO.add(btUpdate, 0, 0);
					System.out.println("Finished");
				}
				else if (currPane == "paneCD") {
					// If it's a deck's data
					deckName = txtName.getText();
					heroName = txtHeroName.getText();
					//??? Array of 40 cards should be here; loop may be required
					updateRowDecks(deckName, heroName, cards, dValue);
					primaryStage.getScene().setRoot(paneMM);
					paneNO.add(btFinish, 2, 24);paneCD.add(btFinish, 0, 24);paneNO.add(btCancel, 2, 24);paneCD.add(btCancel, 2, 24);
					paneNO.add(btUpdate, 0, 0);
					System.out.println("Finished");
				}
				else {/* Something went wrong */}
			}
		};

		// Apply the event handlers to their corresponding buttons
		btFinish.setOnAction(handlerFinish);
		btExit.setOnAction(handlerExit);
		btEdit.setOnAction(handlerEdit);
		btUpdate.setOnAction(handlerUpdate);

	}

		/** SQL commands - insert add a new entry with the given data from existing variables
		 * delete removes an entry from the database
		 * update replaces a database entry's old data with new data
		 * select retrieves data from the database (to be used in the creation/update menus)
		 *  **/
		public static void insertRowCards (String cN, int cT, int at, int hp, String tN, int rt, int eT, int eP, int eP2, int uT) {
			String sql = "INSERT INTO cards VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = null;
			if (cN != "") {
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, cN);//cardName;
				preparedStatement.setInt(2, cT);//cardType;
				preparedStatement.setInt(3, at);//cardAttack;
				preparedStatement.setInt(4, hp);//cardHealth;
				preparedStatement.setString(5, tN);//tagName;
				preparedStatement.setInt(6, rt);//cardRLvl;
				preparedStatement.setInt(7, eT);//eType;
				preparedStatement.setInt(8, eP);//ePower;
				preparedStatement.setInt(9, eP2);//epower2;
				preparedStatement.setInt(10, uT);//cardUType;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to insert new row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to insert new row");
	    		e.printStackTrace();
			}}
		} //end  insertNewRow
		public static void insertRowAbilities (String aN, int eT, int eP, int eP2) {
			String sql = "INSERT INTO abilities VALUES(?, ?, ?, ?)";
			PreparedStatement preparedStatement = null;
			if (aN != "") {
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, aN);//abilName;
				preparedStatement.setInt(2, eT);//eType;
				preparedStatement.setInt(3, eP);//ePower;
				preparedStatement.setInt(4, eP2);//epower2;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to insert new row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to insert new row");
	    		e.printStackTrace();
			}}
		} //end  insertNewRow
		public static void insertRowHeroes (String hN, String aN, int pPT, int at, int hp, int hR, String tL) {
			String sql = "INSERT INTO heroes VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = null;
			if (hN != "") {
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, hN);//cardName;
				preparedStatement.setString(2, aN);//abilName;
				preparedStatement.setInt(3, pPT);//points gained per turn/ppt;
				preparedStatement.setInt(4, at);//heroAttack;
				preparedStatement.setInt(5, hp);//heroHealth;
				preparedStatement.setInt(6, hR);//hero health regen;
				preparedStatement.setString(7, tL);//hero trait list;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to insert new row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to insert new row");
	    		e.printStackTrace();
			}}
		} //end  insertNewRow
		public static void insertRowDecks (String dN, String hN, String cL, double dV) {
			String sql = "INSERT INTO decks VALUES(?, ?, ?, ?)";
			PreparedStatement preparedStatement = null;
			if (dN != "") {
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, dN);//deckName;
				preparedStatement.setString(2, hN);//heroName;
				preparedStatement.setString(3, cL);//card list;
				preparedStatement.setDouble(4, dV);//deck value;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to insert new row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to insert new row");
	    		e.printStackTrace();
			}}
		} //end  insertNewRow
		public static void deleteRowCards (String cName){
			String sql = "DELETE FROM cards WHERE cName = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, cName);
				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to delete row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to delete row");
	    		e.printStackTrace();
			}
		}  //end deleteRow
		public static void deleteRowAbilities (String aName){
			String sql = "DELETE FROM abilities WHERE aName = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, aName);
				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to delete row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to delete row");
	    		e.printStackTrace();
			}
		}  //end deleteRow
		public static void deleteRowHeroes (String hName){
			String sql = "DELETE FROM heroes WHERE hName = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, hName);
				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to delete row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to delete row");
	    		e.printStackTrace();
			}
		}  //end deleteRow
		public static void deleteRowDecks (String dName){
			String sql = "DELETE FROM decks WHERE dName = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, dName);
				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to delete row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to delete row");
	    		e.printStackTrace();
			}
		}  //end deleteRow
		public static void updateRowCards (String cN, int cT, int at, int hp, String tN, int rt, int eT, int eP, int eP2, int uT){
			String sql = "UPDATE cards SET cname = ?, ctype = ?, attack = ?, health = ?, tname = ?, rarity = ?, etype, epower, epower2, eusage WHERE cname = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);

				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, cN);//cardName;
				preparedStatement.setInt(2, cT);//cardType;
				preparedStatement.setInt(3, at);//cardAttack;
				preparedStatement.setInt(4, hp);//cardHealth;
				preparedStatement.setString(5, tN);//tagName;
				preparedStatement.setInt(6, rt);//cardRLvl;
				preparedStatement.setInt(7, eT);//eType;
				preparedStatement.setInt(8, eP);//ePower;
				preparedStatement.setInt(9, eP2);//epower2;
				preparedStatement.setInt(10, uT);//cardUType;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to update  row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to update  row");
	    		e.printStackTrace();
			}
		} //end updateRow
		public static void updateRowAbilities (String aN, int eT, int eP, int eP2){
			String sql = "UPDATE abilities SET aname = ?, etype = ?, epower ?, epower2 = ? WHERE aname = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, aN);//abilName;
				preparedStatement.setInt(2, eT);//eType;
				preparedStatement.setInt(3, eP);//ePower;
				preparedStatement.setInt(4, eP2);//epower2;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to update  row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to update  row");
	    		e.printStackTrace();
			}
		} //end updateRow
		public static void updateRowHeroes (String hN, String aN, int pPT, int at, int hp, int hR, String tL){
			String sql = "UPDATE heroes SET hname = ?, ability = ?, ppt = ?, health = ?, attack = ?, regen = ?, traits = ? WHERE name = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, hN);//cardName;
				preparedStatement.setString(2, aN);//abilName;
				preparedStatement.setInt(3, pPT);//points gained per turn/ppt;
				preparedStatement.setInt(4, at);//heroAttack;
				preparedStatement.setInt(5, hp);//heroHealth;
				preparedStatement.setInt(6, hR);//hero health regen;
				preparedStatement.setString(7, tL);//hero trait list;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to update  row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to update  row");
	    		e.printStackTrace();
			}
		} //end updateRow
		public static void updateRowDecks (String dN, String hN, String cL, double dV){
			String sql = "UPDATE decks SET dname = ?, hero = ?, cards = ?, dvalue = ? WHERE dname = ?";
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, dN);//deckName;
				preparedStatement.setString(2, hN);//heroName;
				preparedStatement.setString(3, cL);//card List;
				preparedStatement.setDouble(4, dV);//deck Value;

				int rowCount = preparedStatement.executeUpdate();
				if (rowCount != 1){
					System.out.println("Error trying to update  row");
				}
			} catch (Exception e) {
				System.out.println("Error trying to update  row");
	    		e.printStackTrace();
			}
		} //end updateRow
		public static void selectRowCards (String cN) {
			PreparedStatement preparedStatement = null;
			String sql = "SELECT * FROM cards WHERE cname = ?";

			ResultSet rs = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, cN);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString(1) +
							", " + rs.getInt(2) +
							", " + rs.getInt(3) +
							", " + rs.getInt(4) +
							", " + rs.getString(5) +
							", " + rs.getInt(6) +
							", " + rs.getInt(7) +
							", " + rs.getInt(8) +
							", " + rs.getInt(9) +
							", " + rs.getInt(10)
							);
				}
			} catch (Exception e) {
				System.out.println("Error trying to select row");
				error = true;
	    		//e.printStackTrace();
			}
		}  // end selectRow
		public static void selectRowAbilities (String aN) {
			PreparedStatement preparedStatement = null;
			String sql = "SELECT * FROM abilities WHERE aname = ?";

			ResultSet rs = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, aN);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString(1) +
							", " + rs.getInt(2) +
							", " + rs.getInt(3) +
							", " + rs.getInt(4));
				}
			} catch (Exception e) {
				System.out.println("Error trying to select  row");
				error = true;
	    		//e.printStackTrace();
			}
		}  // end selectRow
		public static void selectRowHeroes (String hN) {
			PreparedStatement preparedStatement = null;
			String sql = "SELECT * FROM heroes WHERE hname = ?";

			ResultSet rs = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, hN);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString(1) +
							", " + rs.getString(2) +
							", " + rs.getInt(3) +
							", " + rs.getInt(4) +
							", " + rs.getInt(5) +
							", " + rs.getInt(6) +
							", " + rs.getString(7)
							);
				}
			} catch (Exception e) {
				System.out.println("Error trying to select  row");
				error = true;
	    		//e.printStackTrace();
			}
		}  // end selectRow
		public static void selectRowDecks (String dN) {
			PreparedStatement preparedStatement = null;
			String sql = "SELECT * FROM decks WHERE dname = ?";

			ResultSet rs = null;
			try {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, dN);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString(1) +
							", " + rs.getString(2) +
							", " + rs.getString(3) +
							", " + rs.getDouble(4)
							);
				}
			} catch (Exception e) {
				System.out.println("Error trying to select  row");
				error = true;
	    		//e.printStackTrace();
			}
		}  // end selectRow
}
