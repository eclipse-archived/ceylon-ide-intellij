class ClsLevel1() {
	
	interface Level2 {
		class ClsLevel3() {
		}

		void methodLevel3() {}
	}

	void methodLevel2() {}
}

void methodLevel1() {
    ClsLevel1 plop;
}