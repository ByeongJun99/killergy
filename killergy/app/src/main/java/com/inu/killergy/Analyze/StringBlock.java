package com.inu.killergy.Analyze;

import com.google.api.services.vision.v1.model.Vertex;

public class StringBlock {
    private Vertex leftTop; //좌상단
    private Vertex rightTop; //우상단
    private Vertex rightBottom; //우하단
    private Vertex leftBottom; // 좌하단
    private String text; //텍스트 내용

    public StringBlock(){}
    public StringBlock(Vertex _leftTop, Vertex _rightTop, Vertex _rightBottom, Vertex _leftBottom, String _text){
        this.leftTop = _leftTop;
        this.rightTop = _rightTop;
        this.rightBottom = _rightBottom;
        this.leftBottom = _leftBottom;
        this.text = _text;
        if(leftTop.getX() == null) {leftTop.setX(0);}
        if(leftBottom.getX() == null) {leftBottom.setX(0);}
        if(rightTop.getY() == null) {rightTop.setY(0);}
        if(rightBottom.getY() == null) {rightBottom.setY(0);}
        if(rightTop.getX() == null) {rightTop.setX(0);}
        if(rightBottom.getX() == null) {rightBottom.setX(0);}
    }

    public String getText(){return text;}
    public int getMiddleX(){return Math.round((leftTop.getX() + rightBottom.getX())/2);}
    public int getMiddleY(){return Math.round((leftTop.getY() + rightBottom.getY())/2);}
    public int getTop(){return Math.round((leftTop.getY() + rightTop.getY())/2);}
    public int getBottom(){return Math.round((leftBottom.getY() + rightBottom.getY())/2);}
    public int getLeft(){return Math.round((leftTop.getX() + leftBottom.getX())/2);}
    public int getRight(){return Math.round((rightTop.getX() + rightBottom.getX())/2);}
    /*기울어진 형태*/
    public int getRightTopX(){return rightTop.getX();}
    public int getRightTopY(){return rightTop.getY();}
    public int getRightBottomX(){return rightBottom.getX();}
    public int getRightBottomY(){return rightBottom.getY();}
    public int getLeftTopX(){return leftTop.getX();}
    public int getLeftTopY(){return leftTop.getY();}
    public int getLeftBottomX(){return leftBottom.getX();}
    public int getLeftBottomY(){return leftBottom.getY();}
    public Vertex getRightTop(){return rightTop;}
    public Vertex getRightBottom(){return rightBottom;}

    public float getMiddleLeftX(){return (leftTop.getX() + leftBottom.getX())/2;}
    public float getMiddleLeftY(){return (leftTop.getY() + leftBottom.getY())/2;}
    public float getMiddleRightX(){return Math.round((rightTop.getX() + rightBottom.getX())/2);}
    public float getMiddleRightY(){return Math.round((rightTop.getY() + rightBottom.getY())/2);}

    public float getInclination(){
        float dX = getMiddleRightX() - getMiddleLeftX();
        float dY = getMiddleRightY() - getMiddleLeftY();

        if(dX == 0)return 0;
        if(dY == 0)return -999;
        return (dY/dX);
    }

    public float getYIntercept(){
        if(getInclination() == 0)
            return getMiddleLeftX();
        if(getInclination() == -999)
            return getMiddleLeftY();
        return getMiddleLeftY() - getInclination() * getMiddleLeftX();
    }

    //이마가 추가
    public Vertex getLeftTop(){return leftTop;}

    public float getMiddleTopX(){return (leftTop.getX() + rightTop.getX())/2;}
    public float getMiddleTopY(){return (leftTop.getY() + rightTop.getY())/2;}
    public float getMiddleBottomX(){return (leftBottom.getX() + rightBottom.getX())/2;}
    public float getMiddleBottomY(){return (leftBottom.getY() + rightBottom.getY())/2;}

    public float getInclination2(){
        float dX = getMiddleTopX() - getMiddleBottomX();
        float dY = getMiddleTopY() - getMiddleBottomY();
        if(dX == 0){return 0;}
        return (dY/dX);
    }

    public float getInclination3(){
        float dX = getLeftTopX() - getLeftBottomX();
        float dY = getLeftTopY() - getLeftBottomY();
        if(dX == 0){return 0;}
        return (dY/dX);
    }

    public float getYIntercept2() {
        if(getInclination2() == 0){
            return getMiddleTopX();
        }
        return getMiddleTopY() - getInclination2() * getMiddleTopX();
    }

    public float getYIntercept3(){
        if(getInclination3() == 0){
            return getLeftTopX();
        }
        return getLeftTopY() - getInclination3() * getLeftTopX();
    }
    //이마가 끝
}
