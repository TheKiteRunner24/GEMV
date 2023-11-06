package SA

import chisel3._

class Multiplier(val IN_WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    val in_a = Input(SInt(IN_WIDTH.W))
    val in_b = Input(SInt(IN_WIDTH.W))

    val out_c = Output(SInt((2 * IN_WIDTH).W))
  })

  io.out_c := io.in_a * io.in_b
}