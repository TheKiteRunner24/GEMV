package test.scala

import SA._
import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._

class test_TPE extends AnyFreeSpec with ChiselScalatestTester {

  "test_multiplier" in {
    test(new Multiplier(8)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.in_a.poke(2.S)
      dut.io.in_b.poke(4.S)
      dut.clock.step()


    }
  }
}

