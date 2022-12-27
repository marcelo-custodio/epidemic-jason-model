periodoTransmissao(5).
periodoIncubacao(6).

//!colocar_mascara.
//!vacinar.
/* Plans */

/*+!colocar_mascara: mascara(M) <-
	colocar_mascara(M).

+!vacinar: vacina(V) <-
	vacinar(V).
*/

+step(Z) : infectado & infectadoStep(X)[_] & periodoIncubacao(Y) & periodoTransmissao(W) & (Z > X+Y+W) <-
	remover;
	mover.

+step(Z) : infectado & infectadoStep(X)[_] & periodoIncubacao(Y) & (Z >= X+Y)<-
	transmitir;
	mover.

+step(_) <- mover.

+infectado[source(percept)]: not(infectadoStep(_)[source(memory)]) & not(removido) & step(X) <-
	//.print("Infectado em ", X, "\n");
	+infectadoStep(X)[source(memory)].