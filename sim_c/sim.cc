#include <iostream>
#include <bitset>

#include <verilated.h>
#include "Vtop.h"
#ifdef VCD
	#include "verilated_vcd_c.h"
	VerilatedVcdC* tfp = nullptr;
#endif

#include "sim.h"

using namespace std; 

// init pointers
const std::unique_ptr<VerilatedContext> contextp{new VerilatedContext};
const std::unique_ptr<Vtop> top{new Vtop{contextp.get(), "TOP"}};

/* sim initial */
void sim_init(int argc, char *argv[]) {
	top->reset = 1;
	top->clock = 0;
#ifdef VCD
	Verilated::mkdir("wave");
	contextp->traceEverOn(true);
	tfp = new VerilatedVcdC;
	top->trace(tfp, 99); // 99是trace的深度
    tfp->open("wave/top.vcd");
#endif
	Verilated::commandArgs(argc,argv);
}

/* sim exit */
void sim_exit() {
	// finish work, delete pointer
	top->final();
#if VCD
	tfp->close();
	delete tfp;
	tfp = nullptr;
#endif
}

void single_cycle() {
	contextp->timeInc(1);
	top->clock = 1; top->eval();
#ifdef VCD
 tfp->dump(contextp->time());
#endif

	contextp->timeInc(1);
	top->clock = 0; top->eval();
#ifdef VCD
 tfp->dump(contextp->time());
#endif
}

void reset(int n) {
	top->reset = 1;
	while (n-- > 0) single_cycle();
	top->reset = 0;
	top->eval();
}

void sim_main(int argc, char *argv[]) {
	sim_init(argc, argv);
	reset(10);


    top->io_in_control_0_done = 0;
    top->io_in_control_1_done = 0;
    top->io_in_a_0 = 1;
    top->io_in_a_1 = 0;
    top->io_in_b_0 = 5;
    top->io_in_b_1 = 0;

    top->io_in_c_0 = 0;
    top->io_in_c_1 = 0;
    single_cycle();

    top->io_in_control_0_done = 0;
    top->io_in_control_1_done = 0;
    top->io_in_a_0 = 2;
    top->io_in_a_1 = 3;
    top->io_in_b_0 = 7;
    top->io_in_b_1 = 6;

    top->io_in_c_0 = 0;
    top->io_in_c_1 = 0;
    single_cycle();

    top->io_in_control_0_done = 0;
    top->io_in_control_1_done = 0;
    top->io_in_a_0 = 0;
    top->io_in_a_1 = 4;
    top->io_in_b_0 = 0;
    top->io_in_b_1 = 8;

    top->io_in_c_0 = 0;
    top->io_in_c_1 = 0;
    single_cycle();

    top->io_in_control_0_done = 0;
    top->io_in_control_1_done = 0;
    top->io_in_a_0 = 0;
    top->io_in_a_1 = 0;
    top->io_in_b_0 = 0;
    top->io_in_b_1 = 0;

    top->io_in_c_0 = 0;
    top->io_in_c_1 = 0;
    single_cycle();


    int sim_time = 0;
	while (!contextp->gotFinish() && (sim_time <= 10)) {
		single_cycle();
		sim_time++;
	}

	sim_exit();
}
