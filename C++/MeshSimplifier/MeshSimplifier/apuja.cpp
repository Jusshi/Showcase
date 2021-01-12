#include <iostream>

using namespace std;

	//Funktion IsTerminator on palautettava paluuarvona bool - tyyppinen vastaus,
	//joka kertoo onko vakioon osoittavan osoittimen takana olevan merkki terminaattorimerkki vai ei.
	bool IsTerminator(char merkki)
	{
		if (merkki == '\0') {
			return true;
		}
		else {
			return false;
		}
	}
	//Funktio SetTerminator ottaa vastaan viittauksen yhteen merkkiin, ja asetettava sen arvoksi terminaattorimerkki.
	char SetTerminator(char& m)
	{
		m = '\0';
		return m;
	}
	//Funktio IsEmpty ottaa vastaan merkin, ja palauttaa bool - tyyppisen tiedon onko merkki tyhje.
	bool IsEmpty(char merkki)
	{
		if (merkki == NULL) {
			return true;
		}
		else {
			return false;
		}
	}
	//Funktio SetEmpty ottaa vastaan osoittimen merkkiin, ja sijoittaa sen kautta merkiksi terminaattorin.
	void SetEmpty(char &osoitin)
	{
		char *ptr{ &osoitin };
		*ptr = '\0';
	}
	//Funktio IsStar ottaa vastaan vakioviittauksen merkkiin, ja vertaa onko se t‰hti, ja palauttaa bool - tyyppisen arvon.
	bool IsStar(const char& vakiMerkki)
	{
		if (vakiMerkki == '*') {
			return true;
		}
		else {
			return false;
		}
	}
	//Funktio GetAddresses ottaa vastaan osoittimen kokonaislukutaulukkoon, taulukon koon,
	//ja osoittimen toiseen taulukkoon, joka pit‰‰ sis‰ll‰‰n osoittimia kokonaislukuihin.
	//Funktio tallentaa jokaisen ensimm‰isen parametrina tallennetun taulukon sis‰lt‰m‰n kokonaisluvun osoitteen kolmantena parametrina v‰litettyyn taulukkoon.
	/*
	void GetAddresses(int *intTaulu, int koko, int *osTaulu)
	{	
		for (int i = 0; i < koko; i++) {
			int *ptr { intTaulu[i] }
			osTaulu[i] = intTaulu[i]
		}
	}*/