#pragma once
#include <stdexcept>
#include "Vector3.h"
#include "Face.h"
using namespace std;

// virheenk‰sittely-luokka muuntamiseen ep‰sopivien arvojen varalle
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
	// .obj tiedostojen lukemis-j‰senfunktio
	void ReadObjFile(const string& filename);
	// .obj tiedostojen kirjoittamis-j‰senfunktio
	void WriteObjFile(const string& filename);
	// J‰senfunktio joka tutkii mitk‰ kolmioista k‰ytt‰v‰t parametrina annettavaa vertexi‰
	vector<uint32_t> FindShared(int index);
	// J‰senfunktio joka degeneroi kolmiot ja poistaa vertexin
	void EraseVertex(const vector<uint32_t>& faceIndeces, int index);
	// J‰senfunktio joka selvitt‰‰ degeneroidut kolmiot ja poistaa ne
	void SeekAndDestroyDegenerated(int index);
	// J‰senfunktio p‰ivitt‰‰ j‰ljelle j‰‰neet kolmiot
	void updateFaces(int erased);
};