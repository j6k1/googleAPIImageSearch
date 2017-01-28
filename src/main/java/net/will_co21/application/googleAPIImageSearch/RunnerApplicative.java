package net.will_co21.application.googleAPIImageSearch;

public class RunnerApplicative implements Runnable {
	protected Runnable runner;
	@Override
	public void run() {
		if(runner == null) throw new RunnerNotImplementedException("Runnerの実装がオブジェクトに設定されていません。");

		this.runner.run();
	}

	public void runIfImplemented() {
		if(runner != null) runner.run();
	}

	public void setImplements(Runnable r)
	{
		this.runner = r;
	}
}
