#include <iostream>
#include <cstdint>
#include <cmath>
#include "Face.h"
using namespace std;

// jäsenfunktio joka laskee kolmion pinta-alan
float 
Face::CountArea(const Face& face, vector<Vertex> vertexes)
{
	float s1;
	float s2;
	float s3;

	s1 = ((vertexes[(face.ix2 - 1)] - vertexes[(face.ix1 - 1)]).Magnitude());
	s2 = ((vertexes[(face.ix3 - 1)] - vertexes[(face.ix2 - 1)]).Magnitude());
	s3 = ((vertexes[(face.ix1 - 1)] - vertexes[(face.ix3 - 1)]).Magnitude());
	//cout << s1 << ", " << s2 << ", " << s2 << "\n";
	return (sqrt((s1 + s2 + s3) * (s1 + s2 - s3) * (s2 + s3 - s1) * (s3 + s1 - s2)) / 4);
}