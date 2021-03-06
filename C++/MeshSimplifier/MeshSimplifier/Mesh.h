#pragma once
#include <stdexcept>
#include "Vector3.h"
#include "Face.h"
using namespace std;

// virheenkäsittely-luokka muuntamiseen epäsopivien arvojen varalle
class ImproperValueException : public runtime_error {
public:
	ImproperValueException();
};
// Kantaluokka
class Mesh
{
public:
	vector<Face> facet;
	vector<Vertex> verteksit;
	void EraseSmallest();
};
// Mesh-luokan lapsiluokka .obj tiedostojen lukuun ja kirjoittamiseen
class ObjHandler : public Mesh
{
public:
	// .obj tiedostojen lukemis-jäsenfunktio
	void ReadObjFile(const string& filename);
	// .obj tiedostojen kirjoittamis-jäsenfunktio
	void WriteObjFile(const string& filename);
	// Jäsenfunktio joka tutkii mitkä kolmioista käyttävät parametrina annettavaa vertexiä
	vector<uint32_t> FindShared(int index);
	// Jäsenfunktio joka degeneroi kolmiot ja poistaa vertexin
	void EraseVertex(const vector<uint32_t>& faceIndeces, int index);
	// Jäsenfunktio joka selvittää degeneroidut kolmiot ja poistaa ne
	void SeekAndDestroyDegenerated(int index);
	// Jäsenfunktio päivittää jäljelle jääneet kolmiot
	void updateFaces(int erased);
};