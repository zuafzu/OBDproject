package com.qiming.eol_scriptrunner;

public class RunnableWithParams implements Runnable
{
    public interface IRunnableWithParams
    {
        void Run(Object UserParams);
    }

    public RunnableWithParams(Object UserParams,IRunnableWithParams iRunnableWithParams )
    {
        this.UserParams = UserParams;
        this.IRun = iRunnableWithParams;
    }

    Object UserParams = null;
    IRunnableWithParams IRun = null;
    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        if(IRun!=null)
        {
            IRun.Run(UserParams);
        }
    }

}