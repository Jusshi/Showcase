#pragma once
#include <iostream>

class Vector3
{
public:
	/// X-komponentti
	float X;
	/// Y-komponentti
	float Y;
	/// Z-komponentti
	float Z;
	//konstruktori
	Vector3();
	// kopiomuodostin
	Vector3(const Vector3& v);
	//Lisäys jäsenfunktio
	Vector3 Add(const Vector3& v);
	//vähennys jäsenfunktio
	Vector3 Decrease(const Vector3 & v);
	// + operaattorin ylikirjoitus
	Vector3 operator+(const Vector3 & v);
	// - operaattorin ylikirjoitus
	Vector3 operator-(const Vector3 & v);
	// Skaalarilla kertomis jäsenfunktio
	Vector3 Multiply(const Vector3 & v, float skaalari);
	// Skaalarilla jakamis jäsenfunktio
	Vector3 Division(const Vector3 & v, float skaalari);
	//Vektorin pituuden lasku jäsenfunktio
	float Magnitude();
};
// << operaattorin ylikirjoitus
inline std::ostream & operator<<(std::ostream & out, const Vector3 & viittaus)
{
	out << viittaus.X << ", "
		<< viittaus.Y << ", "
		<< viittaus.Z;
	return out;
}
typedef Vector3 Vertex;