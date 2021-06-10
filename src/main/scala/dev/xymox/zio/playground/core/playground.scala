package dev.xymox.zio.playground.core

import zio.Has

package object playground {
  type Example         = Has[Example.Service]
  type AccountObserver = Has[AccountObserver.Service]
}
