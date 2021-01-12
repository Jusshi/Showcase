#include <iostream>
#include <vector>
#include <string>
#include <memory>
#include <fstream>//tiedostonk‰sittelyluokat mukaan
#include <sstream>
#include <cstdint>
#include <set>
#include "Mesh.h"
using namespace std;

// virheenk‰sittely-luokka muuntamiseen ep‰sopivien arvojen varalle
ImproperValueException::ImproperValueException() : runtime_error("Luettua arvoa ei voida muuntaa.") {};

// j‰senfunktio joka selvitt‰‰ ja poistaa pienimm‰n kolmion objektista
void
Mesh::EraseSmallest()
{
	float area;
	float smallest = NULL;
	uint32_t toErase;
	for (int f = 0; f < facet.size(); f++)
	{
		area = facet[f].CountArea(facet[f], verteksit);
		if (!smallest == NULL && area < smallest)
		{
			smallest = area;
			toErase = f;
			cout << toErase << ": " << area << "\n";
		}
		else if (smallest == NULL)
		{
			smallest = area;
			toErase = f;
			cout << toErase << ": " << area << "\n";
		}
	}
	facet.erase(facet.begin() + toErase);
}

// .obj tiedostojen lukemis-j‰senfunktio
void
ObjHandler::ReadObjFile(const string& filename)
{
	fstream fs(filename, fstream::in);
	//^sama asia kuin: ifstream fs(filename); 
	if (fs)
	{
	while (!fs.eof()) //kunnes tule tiedoston loppu "end of file"
	{
		string line;
		getline(fs, line);
		//verteksien luku ja talteen otto
			if (line[0] == 'v')
			{
				stringstream ss;
				ss << line;
				string tmp;
				float x;
				float y;
				float z;
				ss >> tmp; //luetaan v pois rivin alusta
				// luetaan vertexin komponentit ja heitet‰‰n poikkeus jos jokin arvo ei ole sopiva muunnettavaksi
				ss >> x; // x-koordinaatti
				if (!ss) {
					cerr << "X-komponentin luku ei onnistunut!\n";
					throw ImproperValueException();
				}
				ss >> y; // y-koordinaatti
				if (ss.fail()) {
					cerr << "Y-komponentin luku ei onnistunut!\n";
					throw ImproperValueException();
				}
				ss >> z; // z-koordinaatti
				if (ss.fail()) {
					cerr << "Z-komponentin luku ei onnistunut!\n";
					throw ImproperValueException();
				}
				//luodaan verteksi-olio ja asetetaan sille luetut arvot
				Vertex verteksi;
				verteksi.X = x;
				verteksi.Y = y;
				verteksi.Z = z;
				//lis‰t‰‰n verteksi-olio vectoriin
				verteksit.push_back(verteksi);
			}
			//Indeksien luku ja talteen otto
			else if (line[0] == 'f')
			{
				stringstream ss;
				ss << line;
				string tmp;
				int ix1;
				int ix2;
				int ix3;
				ss >> tmp; //luetaan f pois rivist‰
				// luetaan indexit ja heitet‰‰n poikkeus jos arvo ei ole sopiva muunnettavaksi
				ss >> ix1; // indeksi 1
				if (!ss) {
					cerr << "Indeksin luku ei onnistunut!\n";
					throw ImproperValueException();
				}
				ss >> ix2; // indeksi 2
				if (ss.fail()) {
					cerr << "Indeksin luku ei onnistunut!\n";
					throw ImproperValueException();
				}
				ss >> ix3; // indeksi 3
				if (ss.fail()) {
					cerr << "Indeksin luku ei onnistunut!\n";
					throw ImproperValueException();
				}
				//luodaan Face-olio ja asetetaan sille luetut arvot
				Face feissi;
				feissi.ix1 = ix1;
				feissi.ix2 = ix2;
				feissi.ix3 = ix3;
				//lis‰t‰‰n Face-olio vectoriin
				facet.push_back(feissi);
			}
		}
		cout << "Tiedoston luku onnistui\n";
	}
	else
	{
		cerr << "Tiedoston luku ei onnistunut!\n";
	}
}
// .obj tiedostojen kirjoittamis-j‰senfunktio
void
ObjHandler::WriteObjFile(const string& filename)
{
	//luodaan tiedosto ja kirjoitetaan sinne jos tiedoston avaus onnistuu
	ofstream outfile {filename};
	if (outfile)
	{
		outfile << "# Mesh-olion sis‰lt‰m‰st‰ datasta kirjoitettu objekti.\n";
		//Kirjoitetaan tiedostoon verteksit
		for (const Vertex& v : verteksit)
		{
			outfile << "v " << v.X << " " << v.Y << " " << v.Z << "\n";
		}
		outfile << "\n";
		//Kirjoitetaan tiedostoon indeksit
		for (const Face& f : facet)
		{
			outfile << "f " << f.ix1 << " " << f.ix2 << " " << f.ix3 << "\n";
		}
		cout << "Tiedoston luonti onnistui.\n";
	}
	//ilmoitetaan jos tiedoston avaus ei onnistu
	else
	{
		cerr << "Tiedoston avaus ei onnistunut.\n";
	}
	//tyhjennet‰‰n puskuri ja suljetaan tiedosto
	outfile.flush();
	outfile.close();
}
// J‰senfunktio joka tutkii mitk‰ kolmioista k‰ytt‰v‰t parametrina annettavaa vertexi‰
vector<uint32_t>
ObjHandler::FindShared(int index)
{
	vector<uint32_t> indeces;	
	for (int f = 0; f < facet.size(); f++)
	{
		if (facet[f].ix1 == index || facet[f].ix2 == index || facet[f].ix3 == index)
		{
			indeces.push_back(f);
		}
	}
	return indeces;
}
// J‰senfunktio joka degeneroi kolmiot ja poistaa vertexin
void
ObjHandler::EraseVertex(const vector<uint32_t>& faceIndeces, int index)
{
	for (auto f : faceIndeces)
	{
		if (facet[f].ix1 == index)
			facet[f].ix1 = facet[f].ix2;
		else if (facet[f].ix2 == index)
			facet[f].ix2 = facet[f].ix3;
		else if (facet[f].ix3 == index)
			facet[f].ix3 = facet[f].ix1;
	}
	verteksit.erase(verteksit.begin() + (index-1));
	SeekAndDestroyDegenerated(index);
}
// J‰senfunktio joka selvitt‰‰ degeneroidut kolmiot ja poistaa ne
void
ObjHandler::SeekAndDestroyDegenerated(int index)
{
	set<int> faceSet;
	vector<int> indeces;
	for (int i = 0; i < facet.size(); i++)
	{
		faceSet.insert(facet[i].ix1);
		faceSet.insert(facet[i].ix2);
		faceSet.insert(facet[i].ix3);
		if (faceSet.size() < 3)
		{
			indeces.push_back(i);
		}
		faceSet.clear();
	}
	vector<int>::reverse_iterator it = indeces.rbegin();
	while (it != indeces.rend())
	{
	facet.erase(facet.begin() + *it);
	it++;
	}
	updateFaces(index);
}
// J‰senfunktio p‰ivitt‰‰ j‰ljelle j‰‰neet kolmiot
void
ObjHandler::updateFaces(int erased)
{
	for (int i = 0; i < facet.size(); i++)
	{
		if (facet[i].ix1 > erased)
			facet[i].ix1--;
		if (facet[i].ix2 > erased)
			facet[i].ix2--;
		if (facet[i].ix3 > erased)
			facet[i].ix3--;
	}
}