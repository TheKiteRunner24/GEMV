package SA

import chisel3._

// tensor PE
class TPE(val IN_WIDTH: Int, val C_WIDTH: Int, val DP_WIDTH:Int, val TPE_WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    val in_control = Input(Vec(TPE_WIDTH, new DPcontrolIO))
    val in_data = Input(Vec(TPE_WIDTH, new DPdataIO(IN_WIDTH, C_WIDTH, DP_WIDTH)))

    val out_control = Output(Vec(TPE_WIDTH, new DPcontrolIO))
    val out_data = Output(Vec(TPE_WIDTH, new DPdataIO(IN_WIDTH, C_WIDTH, DP_WIDTH)))
  })
  val dp = Seq.fill(TPE_WIDTH, TPE_WIDTH)(Module(new DP(IN_WIDTH, C_WIDTH, DP_WIDTH)))
  val dp_t = dp.transpose

  for (r <- 0 until TPE_WIDTH) {
    dp(r).foldLeft(io.in_data(r).a) {
      case (in_a, dp) =>
        dp.io.in_data.a := in_a
        dp.io.out_data.a
    }
  }

  for (c <- 0 until TPE_WIDTH) {
    dp_t(c).foldLeft(io.in_data(c).b) {
      case (in_b, dp) =>
        dp.io.in_data.b := in_b
        dp.io.out_data.b
    }
  }

  for (c <- 0 until TPE_WIDTH) {
    dp_t(c).foldLeft(io.in_data(c).c) {
      case (in_c, dp) =>
        dp.io.in_data.c := in_c
        dp.io.out_data.c
    }
  }

  for (c <- 0 until TPE_WIDTH) {
    dp_t(c).foldLeft(io.in_control(c)) {
      case (in_ctrl, dp) =>
        dp.io.in_control := in_ctrl
        dp.io.out_control
    }
  }

  val reg_a = Reg(Vec(TPE_WIDTH, Vec(DP_WIDTH, SInt(IN_WIDTH.W))))
  val reg_b = Reg(Vec(TPE_WIDTH, Vec(DP_WIDTH, SInt(IN_WIDTH.W))))

  for (i <- 0 until TPE_WIDTH) {
    for (j <- 0 until DP_WIDTH) {
      reg_a(i)(j) := dp(TPE_WIDTH - 1)(i).io.out_data.a(j)
      io.out_data(i).a(j) := reg_a(i)(j)
    }
  }

  for (i <- 0 until TPE_WIDTH) {
    for (j <- 0 until DP_WIDTH) {
      reg_b(i)(j) := dp_t(TPE_WIDTH - 1)(i).io.out_data.b(j)
      io.out_data(i).b(j) := reg_b(i)(j)
    }
  }

  for (c <- 0 until TPE_WIDTH) {
    io.out_data(c).c := dp_t(TPE_WIDTH - 1)(c).io.out_data.c
    io.out_control(c) := dp_t(TPE_WIDTH - 1)(c).io.out_control
  }

}