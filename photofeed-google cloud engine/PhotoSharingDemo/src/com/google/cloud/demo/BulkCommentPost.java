/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.demo;

import com.google.cloud.demo.model.Comment;
import com.google.cloud.demo.model.CommentManager;
import com.google.cloud.demo.model.Photo;
import com.google.cloud.demo.model.PhotoManager;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BulkCommentPost extends HttpServlet {
  private static final long serialVersionUID = 1L;
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.getWriter().write("SUCCESS");
		
	}
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    AppContext appContext = AppContext.getAppContext();
    String id = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_PHOTO_ID);
    String user = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_PHOTO_OWNER_ID);
    String content = req.getParameter(ServletUtils.REQUEST_PARAM_NAME_COMMENT);
    boolean succeeded = false;
    Long photoId = ServletUtils.validatePhotoId(id);
    StringBuilder builder = new StringBuilder();
    if (photoId != null && user != null && content != null) {
      content = content.trim();
      if (!content.isEmpty()) {
        PhotoManager photoManager = appContext.getPhotoManager();
        Photo photo = photoManager.getPhoto(user, photoId);
        if (photo != null) {
          CommentManager commentManager = appContext.getCommentManager();
          Comment comment = commentManager.newComment(req.getParameter("currentUser"));
          comment.setPhotoId(photoId);
          comment.setPhotoOwnerId(user);
          comment.setTimestamp(System.currentTimeMillis());
          comment.setContent(content);
          comment.setCommentOwnerName("Kiran");
          commentManager.upsertEntity(comment);
          succeeded = true;
        } else {
          builder.append("Request cannot be handled.");
        }
      } else {
        builder.append("Comment could not be empty");
      }
    } else {
      builder.append("Bad parameters");
    }
    if (succeeded) {
    	res.getWriter().write("SUCCESS. Photo Id = "+id);
    } else {
      res.sendError(400, builder.toString());
    }
  }
}
