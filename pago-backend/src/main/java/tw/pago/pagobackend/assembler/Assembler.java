package tw.pago.pagobackend.assembler;

public interface Assembler<S, T> {
  T assemble(S source);

}
