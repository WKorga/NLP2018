# Zadanie 3
Program losuje sekwencję rzutów kostką o długości 10000, po czym na podstawie wyników rzutów próbuje odtworzyć przebieg losowania 
tzn dla każdego rzutu "odgadnąć" czy kość była fałszywa.

Do tego celu używane są dwa algorytmy - heurystyczny i forward-backward

## Algorytm heurystyczny
Algorytm heurystyczny polega na przesuwaniu "okna" o określonej długości po wylosowanej sekwencji rzutów 
i zliczaniu wystepujących w nim szóstek. W momencie, w którym ich liczba przekroczy ustaloną wartość, rzuty w oknie zostają oznaczone
jako wykonane fałszywą kostką.

Jako, że okno jest przesuwane w każdej iteracji pętli o jedną pozycję w prawo, większość rzutów zostanie "sprawdzona" więcej niż raz.
Z tego powodu końcowym wynikiem dla danego rzutu jest to oznaczenie, które pojawiało się częściej.

## Forward-backward
Implementacja algorytmu opisanego m.in. [na Wikipedii](https://en.wikipedia.org/wiki/Forward%E2%80%93backward_algorithm)

Potrzebne wartości:
* Kość oszukana daje 6 oczek z p = 0.5, a pozostałe wyniki z p = 0.1
* Kość prawidłowa daje każdą ilość oczek z prawdopodobieństwem 1/6
* Krupier zmienia kość uczciwą na nieuczciwą z p1 = 0.04, a nieuczciwą na uczciwą z p2 = 0.05
* Zaczynamy od uczciwej kości.

## Wyniki
