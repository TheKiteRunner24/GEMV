package SA

import chisel3._

class DPcontrolIO extends Bundle {
  val done = Bool()
}

// TODO 横向dataflow和纵向dataflow分离
class DPdataIO(val IN_WIDTH: Int, val C_WIDTH: Int, val DP_WIDTH: Int) extends Bundle {
  val a = Vec(DP_WIDTH, SInt(IN_WIDTH.W))
  val b = Vec(DP_WIDTH, SInt(IN_WIDTH.W))
  val c = SInt(C_WIDTH.W)
}

// Dot Product
class DP(val IN_WIDTH: Int, val C_WIDTH: Int, val DP_WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    val in_control = Input(new DPcontrolIO)
    val in_data = Input(new DPdataIO(IN_WIDTH, C_WIDTH, DP_WIDTH))

    val out_control = Output(new DPcontrolIO)
    val out_data = Output(new DPdataIO(IN_WIDTH, C_WIDTH, DP_WIDTH))
  })

  val multiplier = Seq.fill(DP_WIDTH)(Module(new Multiplier(8)))

  for(i <- 0 until DP_WIDTH) {
    multiplier(i).io.in_a := io.in_data.a(i)
    multiplier(i).io.in_b := io.in_data.b(i)
  }

  val multiplier_out_vec = VecInit(multiplier.map(_.io.out_c))

  val sum = multiplier_out_vec.reduce(_ +& _)

  val acc_reg = RegInit(0.S(C_WIDTH.W))

  acc_reg := Mux(io.in_control.done, io.in_data.c, sum)

  io.out_data.c := acc_reg

  io.out_data.a := io.in_data.a
  io.out_data.b := io.in_data.b
  io.out_control := io.in_control
}